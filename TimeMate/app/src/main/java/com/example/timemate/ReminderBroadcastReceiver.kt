package com.example.timemate

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.timemate.NotificationHelper.showNotification

class ReminderBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderBroadcastReceiver", "Alarm received! Triggering notification...")

        // Call the notification helper to show the notification
        showNotification(context, "Reminder", "It's time to check your tasks!")
    }
}
