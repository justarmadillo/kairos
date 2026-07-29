package com.taha.kairos

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

object FirebaseAppCheckInitializer {
    fun initialize(context: Context) {
        try {
            val app = try {
                FirebaseApp.getInstance()
            } catch (_: IllegalStateException) {
                FirebaseApp.initializeApp(context) ?: return
            }
            FirebaseAppCheck.getInstance(app).installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance(),
                true,
            )
        } catch (error: Exception) {
            // Authorization will fail closed, while the recovery export remains usable.
            Log.e("KairosAppCheck", "App Check initialization failed", error)
        }
    }
}
