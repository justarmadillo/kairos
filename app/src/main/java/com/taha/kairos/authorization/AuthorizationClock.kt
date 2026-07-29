package com.taha.kairos.authorization

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import com.taha.kairos.core.authorization.AuthorizationTime
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supplies the three clocks used to validate an authorization lease.
 *
 * Elapsed realtime prevents ordinary wall-clock changes from extending a lease,
 * wall time detects suspicious clock changes, and boot count makes a reboot
 * require a fresh server authorization.
 */
interface AuthorizationClock {
    fun now(): AuthorizationTime
}

@Singleton
class SystemAuthorizationClock @Inject constructor(
    @ApplicationContext context: Context,
) : AuthorizationClock {
    private val contentResolver = context.contentResolver

    override fun now(): AuthorizationTime = AuthorizationTime(
        epochTimeMs = System.currentTimeMillis(),
        elapsedRealtimeMs = SystemClock.elapsedRealtime(),
        bootCount = Settings.Global.getInt(
            contentResolver,
            Settings.Global.BOOT_COUNT,
            -1,
        ),
    )
}
