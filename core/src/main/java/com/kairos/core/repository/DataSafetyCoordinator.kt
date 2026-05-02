package com.kairos.core.repository

interface DataSafetyCoordinator {
    suspend fun <T> withDataLock(block: suspend () -> T): T
}
