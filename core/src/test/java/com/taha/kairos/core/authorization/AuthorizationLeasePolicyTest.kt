package com.taha.kairos.core.authorization

import com.taha.kairos.core.authorization.AuthorizationLeasePolicy.CLOCK_ROLLBACK_TOLERANCE_MS
import com.taha.kairos.core.authorization.AuthorizationLeasePolicy.MAX_OFFLINE_DURATION_MS
import com.taha.kairos.core.authorization.AuthorizationLeasePolicy.NORMAL_LEASE_DURATION_MS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthorizationLeasePolicyTest {
    private val verifiedEpochMs = 1_800_000_000_000L
    private val verifiedElapsedMs = 10_000L

    @Test
    fun `no cached lease has none status`() {
        val result = AuthorizationLeasePolicy.evaluate(
            lease = null,
            currentDeviceId = DEVICE_ID,
            now = time(),
        )

        assertEquals(CachedAuthorizationStatus.NONE, result.status)
        assertNull(result.ageMs)
        assertEquals(0L, result.remainingMs)
    }

    @Test
    fun `lease for another device has none status`() {
        val result = evaluate(lease = lease(deviceId = "another-device"))

        assertEquals(CachedAuthorizationStatus.NONE, result.status)
        assertNull(result.ageMs)
    }

    @Test
    fun `same boot uses elapsed realtime instead of wall clock`() {
        val age = 2L * 60L * 60L * 1_000L
        val result = evaluate(
            now = time(
                epochTimeMs = verifiedEpochMs + MAX_OFFLINE_DURATION_MS + 1L,
                elapsedRealtimeMs = verifiedElapsedMs + age,
            ),
        )

        assertEquals(CachedAuthorizationStatus.FRESH, result.status)
        assertEquals(age, result.ageMs)
        assertEquals(NORMAL_LEASE_DURATION_MS - age, result.remainingMs)
    }

    @Test
    fun `reboot requires a new server authorization`() {
        val result = evaluate(
            now = time(
                epochTimeMs = verifiedEpochMs + NORMAL_LEASE_DURATION_MS + 1L,
                elapsedRealtimeMs = 1L,
                bootCount = VERIFIED_BOOT_COUNT + 1,
            ),
        )

        assertEquals(CachedAuthorizationStatus.INVALID, result.status)
        assertNull(result.ageMs)
    }

    @Test
    fun `fresh interval ends immediately before 24 hours`() {
        val age = NORMAL_LEASE_DURATION_MS - 1L
        val result = evaluate(now = sameBootAtAge(age))

        assertEquals(CachedAuthorizationStatus.FRESH, result.status)
        assertEquals(age, result.ageMs)
        assertEquals(1L, result.remainingMs)
    }

    @Test
    fun `grace interval starts at exactly 24 hours`() {
        val result = evaluate(now = sameBootAtAge(NORMAL_LEASE_DURATION_MS))

        assertEquals(CachedAuthorizationStatus.GRACE, result.status)
        assertEquals(NORMAL_LEASE_DURATION_MS, result.ageMs)
        assertEquals(AuthorizationLeasePolicy.OFFLINE_GRACE_DURATION_MS, result.remainingMs)
    }

    @Test
    fun `grace interval ends immediately before 48 hours`() {
        val age = MAX_OFFLINE_DURATION_MS - 1L
        val result = evaluate(now = sameBootAtAge(age))

        assertEquals(CachedAuthorizationStatus.GRACE, result.status)
        assertEquals(age, result.ageMs)
        assertEquals(1L, result.remainingMs)
    }

    @Test
    fun `lease expires at exactly 48 hours`() {
        val result = evaluate(now = sameBootAtAge(MAX_OFFLINE_DURATION_MS))

        assertEquals(CachedAuthorizationStatus.EXPIRED, result.status)
        assertEquals(MAX_OFFLINE_DURATION_MS, result.ageMs)
        assertEquals(0L, result.remainingMs)
    }

    @Test
    fun `wall clock rollback at tolerance is accepted on the same boot`() {
        val result = evaluate(
            now = time(
                epochTimeMs = verifiedEpochMs - CLOCK_ROLLBACK_TOLERANCE_MS,
            ),
        )

        assertEquals(CachedAuthorizationStatus.FRESH, result.status)
        assertEquals(0L, result.ageMs)
    }

    @Test
    fun `wall clock rollback beyond tolerance is invalid`() {
        val result = evaluate(
            now = time(
                epochTimeMs = verifiedEpochMs - CLOCK_ROLLBACK_TOLERANCE_MS - 1L,
            ),
        )

        assertEquals(CachedAuthorizationStatus.INVALID, result.status)
        assertNull(result.ageMs)
    }

    @Test
    fun `rollback behind latest observation beyond tolerance is invalid`() {
        val latestObserved = verifiedEpochMs + NORMAL_LEASE_DURATION_MS
        val result = evaluate(
            lease = lease(latestObservedEpochMs = latestObserved),
            now = time(
                epochTimeMs = latestObserved - CLOCK_ROLLBACK_TOLERANCE_MS - 1L,
                elapsedRealtimeMs = verifiedElapsedMs + 1L,
            ),
        )

        assertEquals(CachedAuthorizationStatus.INVALID, result.status)
    }

    @Test
    fun `latest observation rollback at tolerance is accepted`() {
        val latestObserved = verifiedEpochMs + NORMAL_LEASE_DURATION_MS
        val result = evaluate(
            lease = lease(latestObservedEpochMs = latestObserved),
            now = time(
                epochTimeMs = latestObserved - CLOCK_ROLLBACK_TOLERANCE_MS,
                elapsedRealtimeMs = verifiedElapsedMs + 1L,
            ),
        )

        assertEquals(CachedAuthorizationStatus.FRESH, result.status)
    }

    @Test
    fun `elapsed realtime moving backwards on same boot is invalid`() {
        val result = evaluate(
            now = time(elapsedRealtimeMs = verifiedElapsedMs - 1L),
        )

        assertEquals(CachedAuthorizationStatus.INVALID, result.status)
    }

    @Test
    fun `boot count moving backwards is invalid`() {
        val result = evaluate(
            now = time(bootCount = VERIFIED_BOOT_COUNT - 1),
        )

        assertEquals(CachedAuthorizationStatus.INVALID, result.status)
    }

    @Test
    fun `future verification beyond tolerance is invalid`() {
        val futureEpoch = verifiedEpochMs + CLOCK_ROLLBACK_TOLERANCE_MS + 1L
        val result = evaluate(
            lease = lease(
                verifiedAtEpochMs = futureEpoch,
                latestObservedEpochMs = futureEpoch,
            ),
        )

        assertEquals(CachedAuthorizationStatus.INVALID, result.status)
    }

    @Test
    fun `negative time fields are invalid`() {
        assertEquals(
            CachedAuthorizationStatus.INVALID,
            evaluate(now = time(epochTimeMs = -1L)).status,
        )
        assertEquals(
            CachedAuthorizationStatus.INVALID,
            evaluate(lease = lease(verifiedAtElapsedRealtimeMs = -1L)).status,
        )
    }

    @Test
    fun `latest observation before verification is invalid`() {
        val result = evaluate(
            lease = lease(latestObservedEpochMs = verifiedEpochMs - 1L),
        )

        assertEquals(CachedAuthorizationStatus.INVALID, result.status)
    }

    @Test
    fun `blank matching device id is invalid`() {
        val result = AuthorizationLeasePolicy.evaluate(
            lease = lease(deviceId = ""),
            currentDeviceId = "",
            now = time(),
        )

        assertEquals(CachedAuthorizationStatus.INVALID, result.status)
    }

    private fun evaluate(
        lease: AuthorizationLease = lease(),
        now: AuthorizationTime = time(),
    ): CachedAuthorization = AuthorizationLeasePolicy.evaluate(
        lease = lease,
        currentDeviceId = DEVICE_ID,
        now = now,
    )

    private fun lease(
        deviceId: String = DEVICE_ID,
        verifiedAtEpochMs: Long = this.verifiedEpochMs,
        verifiedAtElapsedRealtimeMs: Long = verifiedElapsedMs,
        verifiedBootCount: Int = VERIFIED_BOOT_COUNT,
        latestObservedEpochMs: Long = verifiedAtEpochMs,
    ) = AuthorizationLease(
        deviceId = deviceId,
        verifiedAtEpochMs = verifiedAtEpochMs,
        verifiedAtElapsedRealtimeMs = verifiedAtElapsedRealtimeMs,
        verifiedBootCount = verifiedBootCount,
        latestObservedEpochMs = latestObservedEpochMs,
    )

    private fun time(
        epochTimeMs: Long = verifiedEpochMs,
        elapsedRealtimeMs: Long = verifiedElapsedMs,
        bootCount: Int = VERIFIED_BOOT_COUNT,
    ) = AuthorizationTime(
        epochTimeMs = epochTimeMs,
        elapsedRealtimeMs = elapsedRealtimeMs,
        bootCount = bootCount,
    )

    private fun sameBootAtAge(ageMs: Long) = time(
        epochTimeMs = verifiedEpochMs + ageMs,
        elapsedRealtimeMs = verifiedElapsedMs + ageMs,
    )

    private companion object {
        const val DEVICE_ID = "device-123"
        const val VERIFIED_BOOT_COUNT = 7
    }
}
