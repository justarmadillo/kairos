package com.taha.kairos.data.backup

import com.taha.kairos.core.repository.DataSafetyCoordinator
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSafetyCoordinatorImpl @Inject constructor() : DataSafetyCoordinator {
    private val mutex = Mutex()

    override suspend fun <T> withDataLock(block: suspend () -> T): T {
        if (currentCoroutineContext()[DataLockElement.Key] != null) return block()

        return mutex.withLock {
            withContext(DataLockElement()) {
                block()
            }
        }
    }

    private class DataLockElement : AbstractCoroutineContextElement(Key) {
        companion object Key : CoroutineContext.Key<DataLockElement>
    }
}
