package com.taha.kairos.data.authorization

import android.content.Context
import android.provider.Settings
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.taha.kairos.core.authorization.AuthorizationLease
import com.taha.kairos.core.authorization.AuthorizationTime
import com.taha.kairos.core.authorization.DeviceAuthorizationRepository
import com.taha.kairos.core.authorization.RemoteAuthorizationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

private val Context.deviceAuthorizationDataStore: DataStore<Preferences> by
    preferencesDataStore(name = "device_authorization")

/**
 * Stores the last successful server authorization locally and verifies the current device against
 * Firestore. Firestore's local cache is deliberately bypassed for authorization decisions.
 */
@Singleton
class FirebaseDeviceAuthorizationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : DeviceAuthorizationRepository {

    override val deviceId: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        createDeviceId(context)
    }

    override suspend fun loadLease(): AuthorizationLease? {
        val preferences = context.deviceAuthorizationDataStore.data.first()
        if (preferences[Keys.REQUIRES_SERVER_CHECK] == true) return null
        if (preferences[Keys.DEVICE_ID] != deviceId) return null

        val verifiedAtEpochMs = preferences[Keys.VERIFIED_AT_EPOCH_MS] ?: return null
        val verifiedAtElapsedRealtimeMs =
            preferences[Keys.VERIFIED_AT_ELAPSED_REALTIME_MS] ?: return null
        val verifiedBootCount = preferences[Keys.VERIFIED_BOOT_COUNT] ?: return null

        return AuthorizationLease(
            deviceId = deviceId,
            verifiedAtEpochMs = verifiedAtEpochMs,
            verifiedAtElapsedRealtimeMs = verifiedAtElapsedRealtimeMs,
            verifiedBootCount = verifiedBootCount,
            latestObservedEpochMs = maxOf(
                preferences[Keys.LATEST_OBSERVED_EPOCH_MS] ?: verifiedAtEpochMs,
                verifiedAtEpochMs,
            ),
        )
    }

    override suspend fun saveAuthorized(at: AuthorizationTime) {
        context.deviceAuthorizationDataStore.edit { preferences ->
            if (preferences[Keys.DEVICE_ID] != deviceId) {
                preferences.clear()
            }

            preferences[Keys.DEVICE_ID] = deviceId
            preferences[Keys.VERIFIED_AT_EPOCH_MS] = at.epochTimeMs
            preferences[Keys.VERIFIED_AT_ELAPSED_REALTIME_MS] = at.elapsedRealtimeMs
            preferences[Keys.VERIFIED_BOOT_COUNT] = at.bootCount
            // A server-confirmed authorization is a new trusted clock baseline.
            preferences[Keys.LATEST_OBSERVED_EPOCH_MS] = at.epochTimeMs
            preferences.remove(Keys.REQUIRES_SERVER_CHECK)
        }
    }

    override suspend fun clearLease() {
        context.deviceAuthorizationDataStore.edit { preferences ->
            preferences.clear()
            preferences[Keys.DEVICE_ID] = deviceId
            // Do not merely remove timestamps: this marker makes a denial or
            // fail-closed result survive process death. Only saveAuthorized()
            // removes it after a positive server response.
            preferences[Keys.REQUIRES_SERVER_CHECK] = true
        }
    }

    override suspend fun recordObservation(at: AuthorizationTime) {
        context.deviceAuthorizationDataStore.edit { preferences ->
            if (preferences[Keys.DEVICE_ID] != deviceId) {
                // A restored DataStore from another device must never make its lease valid here.
                preferences.clear()
                preferences[Keys.DEVICE_ID] = deviceId
            }

            preferences[Keys.LATEST_OBSERVED_EPOCH_MS] = maxOf(
                preferences[Keys.LATEST_OBSERVED_EPOCH_MS] ?: Long.MIN_VALUE,
                at.epochTimeMs,
            )
        }
    }

    override suspend fun verifyWithServer(): RemoteAuthorizationResult {
        val firebaseApp = try {
            FirebaseApp.getInstance()
        } catch (_: IllegalStateException) {
            return RemoteAuthorizationResult.Unavailable(
                message = "Firebase is not configured. Add app/google-services.json and rebuild Kairos.",
                mayUseOfflineGrace = false,
            )
        }

        return try {
            val snapshot = withTimeout(SERVER_TIMEOUT_MS) {
                FirebaseFirestore.getInstance(firebaseApp)
                    .collection(AUTHORIZED_DEVICES_COLLECTION)
                    .document(deviceId)
                    .get(Source.SERVER)
                    .awaitWithoutKtx()
            }

            if (snapshot.exists() && snapshot.getBoolean(AUTHORIZED_FIELD) == true) {
                RemoteAuthorizationResult.Authorized
            } else {
                RemoteAuthorizationResult.Denied
            }
        } catch (exception: TimeoutCancellationException) {
            RemoteAuthorizationResult.Unavailable(
                message = "Authorization server timed out.",
                mayUseOfflineGrace = true,
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            unavailableResult(exception)
        }
    }

    private fun unavailableResult(exception: Exception): RemoteAuthorizationResult.Unavailable {
        val firestoreException = exception.findFirestoreException()
        val mayUseOfflineGrace = when (firestoreException?.code) {
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED,
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED,
            FirebaseFirestoreException.Code.ABORTED,
            FirebaseFirestoreException.Code.INTERNAL,
            FirebaseFirestoreException.Code.UNAVAILABLE,
            FirebaseFirestoreException.Code.UNKNOWN,
            -> true

            FirebaseFirestoreException.Code.INVALID_ARGUMENT,
            FirebaseFirestoreException.Code.FAILED_PRECONDITION,
            FirebaseFirestoreException.Code.PERMISSION_DENIED,
            FirebaseFirestoreException.Code.UNAUTHENTICATED,
            -> false

            null -> exception is IOException
            else -> false
        }

        return RemoteAuthorizationResult.Unavailable(
            message = exception.message?.takeIf(String::isNotBlank)
                ?: "The authorization server could not be reached.",
            mayUseOfflineGrace = mayUseOfflineGrace,
        )
    }

    private object Keys {
        val DEVICE_ID = stringPreferencesKey("device_id")
        val VERIFIED_AT_EPOCH_MS = longPreferencesKey("verified_at_epoch_ms")
        val VERIFIED_AT_ELAPSED_REALTIME_MS = longPreferencesKey("verified_at_elapsed_realtime_ms")
        val VERIFIED_BOOT_COUNT = intPreferencesKey("verified_boot_count")
        val LATEST_OBSERVED_EPOCH_MS = longPreferencesKey("latest_observed_epoch_ms")
        val REQUIRES_SERVER_CHECK = booleanPreferencesKey("requires_server_check")
    }

    private companion object {
        const val SERVER_TIMEOUT_MS = 12_000L
        const val AUTHORIZED_DEVICES_COLLECTION = "authorized_devices"
        const val AUTHORIZED_FIELD = "authorized"
        const val FALLBACK_ID_PREFERENCES = "device_identity"
        const val FALLBACK_ID_KEY = "installation_id"

        fun createDeviceId(context: Context): String {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID,
            )?.takeIf { it.isNotBlank() } ?: run {
                val preferences = context.getSharedPreferences(
                    FALLBACK_ID_PREFERENCES,
                    Context.MODE_PRIVATE,
                )
                preferences.getString(FALLBACK_ID_KEY, null) ?: UUID.randomUUID()
                    .toString()
                    .also { generatedId ->
                        preferences.edit()
                            .putString(FALLBACK_ID_KEY, generatedId)
                            .commit()
                    }
            }
            val digest = MessageDigest.getInstance("SHA-256").digest(
                "${context.packageName}:$androidId".toByteArray(StandardCharsets.UTF_8),
            )
            val firstTwentyHexCharacters = buildString(capacity = 20) {
                digest.take(10).forEach { byte ->
                    append(HEX[(byte.toInt() ushr 4) and 0x0F])
                    append(HEX[byte.toInt() and 0x0F])
                }
            }
            return "KAIROS-" + firstTwentyHexCharacters.chunked(4).joinToString("-")
        }

        const val HEX = "0123456789ABCDEF"
    }
}

private suspend fun <T> Task<T>.awaitWithoutKtx(): T =
    suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { completedTask ->
            if (!continuation.isActive) return@addOnCompleteListener

            when {
                completedTask.isCanceled -> continuation.cancel()
                completedTask.exception != null ->
                    continuation.resumeWithException(completedTask.exception!!)
                else -> continuation.resumeWith(Result.success(completedTask.result))
            }
        }
    }

private fun Throwable.findFirestoreException(): FirebaseFirestoreException? {
    var current: Throwable? = this
    while (current != null) {
        if (current is FirebaseFirestoreException) return current
        current = current.cause
    }
    return null
}
