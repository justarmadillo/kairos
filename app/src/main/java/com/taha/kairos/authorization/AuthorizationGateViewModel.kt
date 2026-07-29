package com.taha.kairos.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.kairos.core.authorization.AuthorizationLeasePolicy
import com.taha.kairos.core.authorization.AuthorizationTime
import com.taha.kairos.core.authorization.CachedAuthorization
import com.taha.kairos.core.authorization.CachedAuthorizationStatus
import com.taha.kairos.core.authorization.DeviceAuthorizationRepository
import com.taha.kairos.core.authorization.RemoteAuthorizationResult
import com.taha.kairos.core.repository.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

data class AuthorizationGateUiState(
    val deviceId: String,
    val access: AuthorizationAccessState = AuthorizationAccessState.InitialChecking,
    val export: AuthorizationExportState = AuthorizationExportState.Idle,
)

sealed interface AuthorizationAccessState {
    data object InitialChecking : AuthorizationAccessState

    data class Granted(
        val isOfflineGrace: Boolean,
        val isRefreshing: Boolean,
    ) : AuthorizationAccessState

    data class Locked(
        val reason: AuthorizationLockReason,
        val message: String,
        val isRetrying: Boolean = false,
    ) : AuthorizationAccessState
}

enum class AuthorizationLockReason {
    DEVICE_NOT_AUTHORIZED,
    INTERNET_REQUIRED,
    OFFLINE_GRACE_EXPIRED,
    INVALID_LOCAL_AUTHORIZATION,
    SERVER_UNAVAILABLE,
    AUTHORIZATION_CHECK_FAILED,
}

sealed interface AuthorizationExportState {
    data object Idle : AuthorizationExportState
    data object Exporting : AuthorizationExportState
    data class Success(val message: String) : AuthorizationExportState
    data class Failure(val message: String) : AuthorizationExportState
}

@HiltViewModel
class AuthorizationGateViewModel @Inject constructor(
    private val authorizationRepository: DeviceAuthorizationRepository,
    private val backupRepository: BackupRepository,
    private val clock: AuthorizationClock,
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    private val checkMutex = Mutex()
    private var boundaryTimer: Job? = null
    private var hardAccessDeadline: HardAccessDeadline? = null
    private var hardFailureRequiresServer = false

    private val _uiState = MutableStateFlow(
        AuthorizationGateUiState(deviceId = authorizationRepository.deviceId),
    )
    val uiState: StateFlow<AuthorizationGateUiState> = _uiState.asStateFlow()

    init {
        requestEvaluation()

        viewModelScope.launch {
            networkMonitor.isOnline
                .filter { it }
                .collect {
                    requestEvaluation()
                }
        }
    }

    /**
     * Re-evaluates the lease whenever the activity returns to the foreground.
     * Fresh leases remain completely offline; due/locked leases are retried.
     */
    fun onAppResumed() {
        if (shouldHideProtectedContentOnResume()) {
            _uiState.update { it.copy(access = AuthorizationAccessState.InitialChecking) }
        }
        requestEvaluation()
    }

    fun retry() {
        requestEvaluation()
    }

    /**
     * Exports independently of gate state so recovery remains possible even if
     * authorization storage or server-check logic has failed.
     */
    fun exportData(folderUri: String) {
        if (folderUri.isBlank()) {
            _uiState.update {
                it.copy(
                    export = AuthorizationExportState.Failure(
                        "No export folder was selected.",
                    ),
                )
            }
            return
        }
        if (_uiState.value.export == AuthorizationExportState.Exporting) return

        _uiState.update { it.copy(export = AuthorizationExportState.Exporting) }
        viewModelScope.launch {
            val exportState = try {
                val result = backupRepository.export(folderUri)
                if (result.success) {
                    AuthorizationExportState.Success(
                        "Your app backup was saved successfully.",
                    )
                } else {
                    AuthorizationExportState.Failure(
                        result.error?.takeIf(String::isNotBlank)
                            ?.let { "Backup failed: $it" }
                            ?: "The backup could not be created.",
                    )
                }
            } catch (error: Exception) {
                AuthorizationExportState.Failure(
                    error.message?.takeIf(String::isNotBlank)
                        ?.let { "Backup failed: $it" }
                        ?: "The backup could not be created.",
                )
            }

            _uiState.update { it.copy(export = exportState) }
        }
    }

    private fun requestEvaluation() {
        viewModelScope.launch {
            checkMutex.withLock {
                evaluateLease()
            }
        }
    }

    private suspend fun evaluateLease() {
        markCheckStarted()

        val now = try {
            clock.now()
        } catch (_: Exception) {
            lockForUnexpectedFailure()
            return
        }

        val cached = try {
            AuthorizationLeasePolicy.evaluate(
                lease = authorizationRepository.loadLease(),
                currentDeviceId = authorizationRepository.deviceId,
                now = now,
            )
        } catch (_: Exception) {
            lockForUnexpectedFailure()
            return
        }

        if (hardFailureRequiresServer) {
            cancelBoundaryTimer()
            if (networkMonitor.isOnline.value) {
                enterFailClosedCheck()
                verifyRemotely(CachedAuthorization(CachedAuthorizationStatus.NONE))
            } else {
                lock(
                    reason = AuthorizationLockReason.AUTHORIZATION_CHECK_FAILED,
                    message = "Online authorization check required.",
                )
            }
            return
        }

        when (cached.status) {
            CachedAuthorizationStatus.FRESH -> {
                if (!recordTrustedObservation(now)) return
                grant(cached = cached, evaluatedAt = now, isOfflineGrace = false)
            }

            CachedAuthorizationStatus.GRACE -> {
                if (!recordTrustedObservation(now)) return
                grant(
                    cached = cached,
                    evaluatedAt = now,
                    isOfflineGrace = true,
                    isRefreshing = networkMonitor.isOnline.value,
                )
                if (networkMonitor.isOnline.value) {
                    verifyRemotely(cached)
                }
            }

            CachedAuthorizationStatus.NONE,
            CachedAuthorizationStatus.EXPIRED,
            CachedAuthorizationStatus.INVALID,
            -> {
                cancelBoundaryTimer()
                if (networkMonitor.isOnline.value) {
                    // Once the cache is no longer usable, protected content
                    // must disappear before waiting for the remote response.
                    enterFailClosedCheck()
                    verifyRemotely(cached)
                } else {
                    lockWithoutNetwork(cached.status)
                }
            }
        }
    }

    private suspend fun recordTrustedObservation(
        now: AuthorizationTime,
    ): Boolean = try {
        authorizationRepository.recordObservation(now)
        true
    } catch (_: Exception) {
        lockForUnexpectedFailure()
        false
    }

    private suspend fun verifyRemotely(cached: CachedAuthorization) {
        val result = try {
            authorizationRepository.verifyWithServer()
        } catch (_: Exception) {
            lockForUnexpectedFailure()
            return
        }

        when (result) {
            RemoteAuthorizationResult.Authorized -> {
                val verifiedAt = try {
                    clock.now()
                } catch (_: Exception) {
                    lockForUnexpectedFailure()
                    return
                }

                try {
                    authorizationRepository.saveAuthorized(verifiedAt)
                } catch (_: Exception) {
                    lockForUnexpectedFailure()
                    return
                }

                hardFailureRequiresServer = false
                grantFreshLease(verifiedAt)
            }

            RemoteAuthorizationResult.Denied -> {
                cancelBoundaryTimer()
                hardFailureRequiresServer = true
                // The denial takes effect immediately even if clearing local
                // storage itself encounters an error.
                lock(
                    reason = AuthorizationLockReason.DEVICE_NOT_AUTHORIZED,
                    message = "Authorization required.",
                )
                try {
                    authorizationRepository.clearLease()
                } catch (_: Exception) {
                    // Remaining locked is safer than falling back to stale data.
                }
            }

            is RemoteAuthorizationResult.Unavailable -> {
                val canUseGrace =
                    cached.status == CachedAuthorizationStatus.GRACE &&
                        result.mayUseOfflineGrace

                if (canUseGrace) {
                    grantGraceAfterFailedRefresh()
                } else {
                    cancelBoundaryTimer()
                    if (!result.mayUseOfflineGrace) {
                        hardFailureRequiresServer = true
                    }
                    lock(
                        reason = if (result.mayUseOfflineGrace) {
                            AuthorizationLockReason.SERVER_UNAVAILABLE
                        } else {
                            AuthorizationLockReason.AUTHORIZATION_CHECK_FAILED
                        },
                        message = "Authorization check failed.",
                    )
                    if (!result.mayUseOfflineGrace) {
                        try {
                            authorizationRepository.clearLease()
                        } catch (_: Exception) {
                            // The in-memory sticky failure still prevents reuse
                            // for the lifetime of this process.
                        }
                    }
                }
            }
        }
    }

    private suspend fun grantGraceAfterFailedRefresh() {
        val currentTime = try {
            clock.now()
        } catch (_: Exception) {
            lockForUnexpectedFailure()
            return
        }
        val currentCached = try {
            AuthorizationLeasePolicy.evaluate(
                lease = authorizationRepository.loadLease(),
                currentDeviceId = authorizationRepository.deviceId,
                now = currentTime,
            )
        } catch (_: Exception) {
            lockForUnexpectedFailure()
            return
        }

        if (currentCached.status == CachedAuthorizationStatus.GRACE) {
            grant(
                cached = currentCached,
                evaluatedAt = currentTime,
                isOfflineGrace = true,
                isRefreshing = false,
            )
        } else {
            cancelBoundaryTimer()
            lockWithoutNetwork(currentCached.status)
        }
    }

    private fun grantFreshLease(verifiedAt: AuthorizationTime) {
        _uiState.update {
            it.copy(
                access = AuthorizationAccessState.Granted(
                    isOfflineGrace = false,
                    isRefreshing = false,
                ),
            )
        }
        setHardAccessDeadline(
            evaluatedAt = verifiedAt,
            remainingUntilHardExpiryMs = AuthorizationLeasePolicy.MAX_OFFLINE_DURATION_MS,
        )
        scheduleBoundary(AuthorizationLeasePolicy.NORMAL_LEASE_DURATION_MS)
    }

    private fun grant(
        cached: CachedAuthorization,
        evaluatedAt: AuthorizationTime,
        isOfflineGrace: Boolean,
        isRefreshing: Boolean = false,
    ) {
        _uiState.update {
            it.copy(
                access = AuthorizationAccessState.Granted(
                    isOfflineGrace = isOfflineGrace,
                    isRefreshing = isRefreshing,
                ),
            )
        }
        val ageMs = cached.ageMs ?: AuthorizationLeasePolicy.MAX_OFFLINE_DURATION_MS
        setHardAccessDeadline(
            evaluatedAt = evaluatedAt,
            remainingUntilHardExpiryMs =
                (AuthorizationLeasePolicy.MAX_OFFLINE_DURATION_MS - ageMs).coerceAtLeast(0L),
        )
        scheduleBoundary(cached.remainingMs)
    }

    private fun lockWithoutNetwork(status: CachedAuthorizationStatus) {
        when (status) {
            CachedAuthorizationStatus.NONE -> lock(
                reason = AuthorizationLockReason.INTERNET_REQUIRED,
                message = "Connection required.",
            )

            CachedAuthorizationStatus.EXPIRED -> lock(
                reason = AuthorizationLockReason.OFFLINE_GRACE_EXPIRED,
                message = "Connection required.",
            )

            CachedAuthorizationStatus.INVALID -> lock(
                reason = AuthorizationLockReason.INVALID_LOCAL_AUTHORIZATION,
                message = "Authorization check required.",
            )

            else -> lock(
                reason = AuthorizationLockReason.AUTHORIZATION_CHECK_FAILED,
                message = "Device authorization could not be checked.",
            )
        }
    }

    private fun markCheckStarted() {
        _uiState.update { state ->
            state.copy(
                access = when (val access = state.access) {
                    AuthorizationAccessState.InitialChecking -> access
                    is AuthorizationAccessState.Granted ->
                        access.copy(isRefreshing = access.isOfflineGrace)
                    is AuthorizationAccessState.Locked ->
                        access.copy(isRetrying = true)
                },
            )
        }
    }

    private fun enterFailClosedCheck() {
        _uiState.update { state ->
            if (state.access is AuthorizationAccessState.Granted) {
                state.copy(access = AuthorizationAccessState.InitialChecking)
            } else {
                state
            }
        }
    }

    private suspend fun lockForUnexpectedFailure() {
        cancelBoundaryTimer()
        hardFailureRequiresServer = true
        lock(
            reason = AuthorizationLockReason.AUTHORIZATION_CHECK_FAILED,
            message = "Authorization check failed.",
        )
        try {
            authorizationRepository.clearLease()
        } catch (_: Exception) {
            // Access is already fail-closed in memory. Storage failures are
            // deliberately not allowed to reopen the protected UI.
        }
    }

    private fun lock(reason: AuthorizationLockReason, message: String) {
        hardAccessDeadline = null
        _uiState.update {
            it.copy(
                access = AuthorizationAccessState.Locked(
                    reason = reason,
                    message = message,
                ),
            )
        }
    }

    private fun scheduleBoundary(delayMs: Long) {
        boundaryTimer?.cancel()
        boundaryTimer = viewModelScope.launch {
            delay(delayMs.coerceAtLeast(1L))
            requestEvaluation()
        }
    }

    private fun cancelBoundaryTimer() {
        boundaryTimer?.cancel()
        boundaryTimer = null
    }

    private fun setHardAccessDeadline(
        evaluatedAt: AuthorizationTime,
        remainingUntilHardExpiryMs: Long,
    ) {
        hardAccessDeadline = HardAccessDeadline(
            bootCount = evaluatedAt.bootCount,
            elapsedRealtimeMs =
                evaluatedAt.elapsedRealtimeMs + remainingUntilHardExpiryMs,
            observedEpochMs = evaluatedAt.epochTimeMs,
        )
    }

    private fun shouldHideProtectedContentOnResume(): Boolean {
        if (_uiState.value.access !is AuthorizationAccessState.Granted) return false
        val deadline = hardAccessDeadline ?: return true
        val now = try {
            clock.now()
        } catch (_: Exception) {
            return true
        }
        return now.bootCount != deadline.bootCount ||
            now.elapsedRealtimeMs >= deadline.elapsedRealtimeMs ||
            now.epochTimeMs + AuthorizationLeasePolicy.CLOCK_ROLLBACK_TOLERANCE_MS <
                deadline.observedEpochMs
    }

    private data class HardAccessDeadline(
        val bootCount: Int,
        val elapsedRealtimeMs: Long,
        val observedEpochMs: Long,
    )

}
