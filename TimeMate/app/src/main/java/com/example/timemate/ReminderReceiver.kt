package com.example.timemate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Retrieve the reminder message from the intent
        val message = intent.getStringExtra("message") ?: "Don't forget your task!"

        // Create the notification channel for the reminder
        createNotificationChannel(context)

        // Build the notification
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications) // Ensure this icon exists in your drawable folder
            .setContentTitle("Task Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Automatically dismiss the notification when clicked

        // Get NotificationManager system service to show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build()) // You can set a unique ID for multiple notifications
    }

    // Helper function to create the notification channel for Android O and above
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Reminder Channel"
            val channelDescription = "This channel is used for task reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDescription
            }

            // Register the notification channel with the system
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        // Unique channel ID for the notification
        private const val CHANNEL_ID = "reminder_channel"
    }
}
