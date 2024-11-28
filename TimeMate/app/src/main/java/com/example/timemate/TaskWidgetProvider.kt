package com.example.timemate

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class TaskWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        // Load tasks from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val tasks = sharedPreferences.getStringSet("task_keys", null)
        val tasksText = tasks?.joinToString("\n") ?: "No tasks available"

        // Update the widget view
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        views.setTextViewText(R.id.widget_text, tasksText)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
