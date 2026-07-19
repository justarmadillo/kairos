package com.kairos.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.kairos.MainActivity
import com.kairos.R
import com.kairos.navigation.ROUTE_PATIENT_CASE

/**
 * Home-screen quick-capture widget: one tap into the new-case form or global search.
 * Static RemoteViews only — no data binding, no periodic updates.
 */
class QuickCaptureWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { id ->
            appWidgetManager.updateAppWidget(id, buildViews(context))
        }
    }

    private fun buildViews(context: Context): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_quick_capture).apply {
            setOnClickPendingIntent(
                R.id.widget_new_case,
                destinationIntent(context, 0, ROUTE_PATIENT_CASE),
            )
            setOnClickPendingIntent(
                R.id.widget_search,
                destinationIntent(context, 1, "search"),
            )
        }

    private fun destinationIntent(
        context: Context,
        requestCode: Int,
        destination: String,
    ): PendingIntent =
        PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(MainActivity.EXTRA_WIDGET_DESTINATION, destination)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
}
