package com.taha.kairos.core.authorization

object AuthorizationLeasePolicy {
    const val NORMAL_LEASE_DURATION_MS: Long = 24L * 60L * 60L * 1_000L
    const val OFFLINE_GRACE_DURATION_MS: Long = 24L * 60L * 60L * 1_000L
    const val MAX_OFFLINE_DURATION_MS: Long =
        NORMAL_LEASE_DURATION_MS + OFFLINE_GRACE_DURATION_MS
    const val CLOCK_ROLLBACK_TOLERANCE_MS: Long = 5L * 60L * 1_000L

    /**
     * Evaluates a cached authorization without performing I/O.
     *
     * The monotonic clock is authoritative during the boot in which the lease
     * was verified. A reboot invalidates the offline lease because elapsed
     * realtime can no longer be compared safely.
     */
    fun evaluate(
        lease: AuthorizationLease?,
        currentDeviceId: String,
        now: AuthorizationTime,
    ): CachedAuthorization {
        if (lease == null || lease.deviceId != currentDeviceId) {
            return CachedAuthorization(CachedAuthorizationStatus.NONE)
        }

        if (!lease.isStructurallyValid() || !now.isStructurallyValid()) {
            return CachedAuthorization(CachedAuthorizationStatus.INVALID)
        }

        // elapsedRealtime is the only local clock a user cannot roll back.
        // It resets on reboot, so a reboot requires a new server check rather
        // than trusting an adjustable wall clock for an offline lease.
        if (now.bootCount != lease.verifiedBootCount) {
            return CachedAuthorization(CachedAuthorizationStatus.INVALID)
        }

        if (isMateriallyBefore(now.epochTimeMs, lease.verifiedAtEpochMs) ||
            isMateriallyBefore(now.epochTimeMs, lease.latestObservedEpochMs)
        ) {
            return CachedAuthorization(CachedAuthorizationStatus.INVALID)
        }

        if (now.elapsedRealtimeMs < lease.verifiedAtElapsedRealtimeMs) {
            return CachedAuthorization(CachedAuthorizationStatus.INVALID)
        }
        val ageMs = now.elapsedRealtimeMs - lease.verifiedAtElapsedRealtimeMs

        return when {
            ageMs < NORMAL_LEASE_DURATION_MS -> CachedAuthorization(
                status = CachedAuthorizationStatus.FRESH,
                ageMs = ageMs,
                remainingMs = NORMAL_LEASE_DURATION_MS - ageMs,
            )

            ageMs < MAX_OFFLINE_DURATION_MS -> CachedAuthorization(
                status = CachedAuthorizationStatus.GRACE,
                ageMs = ageMs,
                remainingMs = MAX_OFFLINE_DURATION_MS - ageMs,
            )

            else -> CachedAuthorization(
                status = CachedAuthorizationStatus.EXPIRED,
                ageMs = ageMs,
            )
        }
    }

    private fun AuthorizationLease.isStructurallyValid(): Boolean =
        deviceId.isNotBlank() &&
            verifiedAtEpochMs >= 0L &&
            verifiedAtElapsedRealtimeMs >= 0L &&
            verifiedBootCount >= 0 &&
            latestObservedEpochMs >= verifiedAtEpochMs

    private fun AuthorizationTime.isStructurallyValid(): Boolean =
        epochTimeMs >= 0L &&
            elapsedRealtimeMs >= 0L &&
            bootCount >= 0

    private fun isMateriallyBefore(nowMs: Long, trustedMs: Long): Boolean =
        nowMs < trustedMs &&
            trustedMs - nowMs > CLOCK_ROLLBACK_TOLERANCE_MS
}
