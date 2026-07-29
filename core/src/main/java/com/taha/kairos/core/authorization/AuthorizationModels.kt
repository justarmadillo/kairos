package com.taha.kairos.core.authorization

/**
 * A wall-clock and monotonic-clock snapshot taken together.
 *
 * [elapsedRealtimeMs] is only comparable with a value from the same [bootCount].
 * A boot-count change deliberately invalidates an offline lease.
 */
data class AuthorizationTime(
    val epochTimeMs: Long,
    val elapsedRealtimeMs: Long,
    val bootCount: Int,
)

/**
 * The last locally cached, successful server authorization.
 *
 * [latestObservedEpochMs] is the greatest trusted wall-clock time observed while
 * this lease has been stored. It is used to reject material clock rollbacks.
 */
data class AuthorizationLease(
    val deviceId: String,
    val verifiedAtEpochMs: Long,
    val verifiedAtElapsedRealtimeMs: Long,
    val verifiedBootCount: Int,
    val latestObservedEpochMs: Long,
)

enum class CachedAuthorizationStatus {
    NONE,
    FRESH,
    GRACE,
    EXPIRED,
    INVALID,
}

data class CachedAuthorization(
    val status: CachedAuthorizationStatus,
    val ageMs: Long? = null,
    val remainingMs: Long = 0,
)

sealed interface RemoteAuthorizationResult {
    data object Authorized : RemoteAuthorizationResult

    data object Denied : RemoteAuthorizationResult

    data class Unavailable(
        val message: String,
        val mayUseOfflineGrace: Boolean,
    ) : RemoteAuthorizationResult
}

/**
 * Persistence and remote-verification boundary for device authorization.
 *
 * Implementations should persist the lease in storage that is private to the
 * application. A successful [saveAuthorized] starts a new lease and
 * [recordObservation] advances its rollback guard.
 */
interface DeviceAuthorizationRepository {
    val deviceId: String

    suspend fun loadLease(): AuthorizationLease?

    suspend fun saveAuthorized(at: AuthorizationTime)

    /**
     * Invalidates the cached lease and durably requires a successful server
     * authorization before cached access can be used again.
     */
    suspend fun clearLease()

    suspend fun recordObservation(at: AuthorizationTime)

    suspend fun verifyWithServer(): RemoteAuthorizationResult
}
