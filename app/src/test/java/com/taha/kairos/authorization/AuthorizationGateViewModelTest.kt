package com.taha.kairos.authorization

import com.taha.kairos.core.authorization.AuthorizationLease
import com.taha.kairos.core.authorization.AuthorizationLeasePolicy.MAX_OFFLINE_DURATION_MS
import com.taha.kairos.core.authorization.AuthorizationLeasePolicy.NORMAL_LEASE_DURATION_MS
import com.taha.kairos.core.authorization.AuthorizationTime
import com.taha.kairos.core.authorization.DeviceAuthorizationRepository
import com.taha.kairos.core.authorization.RemoteAuthorizationResult
import com.taha.kairos.core.repository.BackupRepository
import com.taha.kairos.core.repository.BackupResult
import com.taha.kairos.core.repository.RestoreResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorizationGateViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `graceable remote failure locks when its 12 seconds cross hard expiry`() = runTest {
        val clock = FakeAuthorizationClock(
            timeAtLeaseAge(MAX_OFFLINE_DURATION_MS - 10_000L),
        )
        val repository = FakeAuthorizationRepository(
            lease = authorizedLease(),
            verify = {
                clock.advanceBy(12_000L)
                RemoteAuthorizationResult.Unavailable(
                    message = "Temporary server failure",
                    mayUseOfflineGrace = true,
                )
            },
        )
        val network = FakeNetworkMonitor(isOnline = false)
        val viewModel = viewModel(repository, clock, network)
        runCurrent()
        assertTrue(viewModel.uiState.value.access is AuthorizationAccessState.Granted)

        network.setOnline(true)
        runCurrent()

        val locked = viewModel.uiState.value.access as AuthorizationAccessState.Locked
        assertEquals(AuthorizationLockReason.OFFLINE_GRACE_EXPIRED, locked.reason)
    }

    @Test
    fun `non-graceable remote failure clears lease and cannot resurrect on offline resume`() =
        runTest {
            val clock = FakeAuthorizationClock(
                timeAtLeaseAge(NORMAL_LEASE_DURATION_MS + 1L),
            )
            val repository = FakeAuthorizationRepository(
                lease = authorizedLease(),
                verify = {
                    RemoteAuthorizationResult.Unavailable(
                        message = "Untrusted authorization response",
                        mayUseOfflineGrace = false,
                    )
                },
            )
            val network = FakeNetworkMonitor(isOnline = false)
            val viewModel = viewModel(repository, clock, network)
            runCurrent()
            assertTrue(viewModel.uiState.value.access is AuthorizationAccessState.Granted)

            network.setOnline(true)
            runCurrent()

            val remoteFailure = viewModel.uiState.value.access as AuthorizationAccessState.Locked
            assertEquals(
                AuthorizationLockReason.AUTHORIZATION_CHECK_FAILED,
                remoteFailure.reason,
            )
            assertNull(repository.lease)
            assertEquals(1, repository.clearLeaseCalls)
            assertEquals(1, repository.verifyCalls)

            network.setOnline(false)
            viewModel.onAppResumed()
            runCurrent()

            val afterOfflineResume =
                viewModel.uiState.value.access as AuthorizationAccessState.Locked
            assertEquals(
                AuthorizationLockReason.AUTHORIZATION_CHECK_FAILED,
                afterOfflineResume.reason,
            )
            assertEquals(1, repository.verifyCalls)
            assertNull(repository.lease)
        }

    @Test
    fun `resume synchronously hides granted content after in-memory hard deadline`() = runTest {
        val clock = FakeAuthorizationClock(timeAtLeaseAge(1L))
        val repository = FakeAuthorizationRepository(lease = authorizedLease())
        val network = FakeNetworkMonitor(isOnline = false)
        val viewModel = viewModel(repository, clock, network)
        runCurrent()
        assertTrue(viewModel.uiState.value.access is AuthorizationAccessState.Granted)

        clock.currentTime = timeAtLeaseAge(MAX_OFFLINE_DURATION_MS)
        viewModel.onAppResumed()

        assertEquals(
            AuthorizationAccessState.InitialChecking,
            viewModel.uiState.value.access,
        )
    }

    private fun viewModel(
        repository: DeviceAuthorizationRepository,
        clock: AuthorizationClock,
        networkMonitor: NetworkMonitor,
    ) = AuthorizationGateViewModel(
        authorizationRepository = repository,
        backupRepository = FakeBackupRepository,
        clock = clock,
        networkMonitor = networkMonitor,
    )

    private class FakeAuthorizationClock(
        var currentTime: AuthorizationTime,
    ) : AuthorizationClock {
        override fun now(): AuthorizationTime = currentTime

        fun advanceBy(durationMs: Long) {
            currentTime = currentTime.copy(
                epochTimeMs = currentTime.epochTimeMs + durationMs,
                elapsedRealtimeMs = currentTime.elapsedRealtimeMs + durationMs,
            )
        }
    }

    private class FakeNetworkMonitor(
        isOnline: Boolean,
    ) : NetworkMonitor {
        private val online = MutableStateFlow(isOnline)
        override val isOnline: StateFlow<Boolean> = online

        fun setOnline(value: Boolean) {
            online.value = value
        }
    }

    private class FakeAuthorizationRepository(
        var lease: AuthorizationLease?,
        private val verify: suspend () -> RemoteAuthorizationResult = {
            RemoteAuthorizationResult.Authorized
        },
    ) : DeviceAuthorizationRepository {
        override val deviceId: String = DEVICE_ID
        var clearLeaseCalls: Int = 0
        var verifyCalls: Int = 0

        override suspend fun loadLease(): AuthorizationLease? = lease

        override suspend fun saveAuthorized(at: AuthorizationTime) {
            lease = AuthorizationLease(
                deviceId = deviceId,
                verifiedAtEpochMs = at.epochTimeMs,
                verifiedAtElapsedRealtimeMs = at.elapsedRealtimeMs,
                verifiedBootCount = at.bootCount,
                latestObservedEpochMs = at.epochTimeMs,
            )
        }

        override suspend fun clearLease() {
            clearLeaseCalls += 1
            lease = null
        }

        override suspend fun recordObservation(at: AuthorizationTime) {
            lease = lease?.copy(
                latestObservedEpochMs = maxOf(
                    lease?.latestObservedEpochMs ?: at.epochTimeMs,
                    at.epochTimeMs,
                ),
            )
        }

        override suspend fun verifyWithServer(): RemoteAuthorizationResult {
            verifyCalls += 1
            return verify()
        }
    }

    private object FakeBackupRepository : BackupRepository {
        override suspend fun export(folderUri: String): BackupResult =
            error("Export is not exercised by authorization state tests")

        override suspend fun restore(zipUri: String): RestoreResult =
            error("Restore is not exercised by authorization state tests")

        override suspend fun vacuumDatabase() {
            error("Vacuum is not exercised by authorization state tests")
        }
    }

    private companion object {
        const val DEVICE_ID = "device-123"
        const val VERIFIED_EPOCH_MS = 1_800_000_000_000L
        const val VERIFIED_ELAPSED_MS = 50_000L
        const val BOOT_COUNT = 7

        fun authorizedLease() = AuthorizationLease(
            deviceId = DEVICE_ID,
            verifiedAtEpochMs = VERIFIED_EPOCH_MS,
            verifiedAtElapsedRealtimeMs = VERIFIED_ELAPSED_MS,
            verifiedBootCount = BOOT_COUNT,
            latestObservedEpochMs = VERIFIED_EPOCH_MS,
        )

        fun timeAtLeaseAge(ageMs: Long) = AuthorizationTime(
            epochTimeMs = VERIFIED_EPOCH_MS + ageMs,
            elapsedRealtimeMs = VERIFIED_ELAPSED_MS + ageMs,
            bootCount = BOOT_COUNT,
        )
    }
}
