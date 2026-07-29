package com.taha.kairos.data.authorization

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import com.taha.kairos.core.authorization.AuthorizationLeasePolicy
import com.taha.kairos.core.authorization.AuthorizationTime
import com.taha.kairos.core.authorization.CachedAuthorizationStatus
import com.taha.kairos.core.authorization.DeviceAuthorizationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fail-closed local gate for background work that can mutate user data.
 *
 * It never renews a lease; the foreground authorization flow owns Firebase
 * checks. Emergency/manual backup export deliberately remains available while
 * locked.
 */
@Singleton
class CachedAuthorizationGuard @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authorizationRepository: DeviceAuthorizationRepository,
) {
    suspend fun hasCachedAccess(): Boolean = try {
        val now = AuthorizationTime(
            epochTimeMs = System.currentTimeMillis(),
            elapsedRealtimeMs = SystemClock.elapsedRealtime(),
            bootCount = Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.BOOT_COUNT,
                -1,
            ),
        )
        AuthorizationLeasePolicy.evaluate(
            lease = authorizationRepository.loadLease(),
            currentDeviceId = authorizationRepository.deviceId,
            now = now,
        ).status in ALLOWED_STATUSES
    } catch (_: Exception) {
        false
    }

    private companion object {
        val ALLOWED_STATUSES = setOf(
            CachedAuthorizationStatus.FRESH,
            CachedAuthorizationStatus.GRACE,
        )
    }
}
