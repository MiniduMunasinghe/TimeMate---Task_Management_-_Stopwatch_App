package com.example.timemate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder) // Ensure correct layout is used

        // Check for exact alarm permission (for Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasExactAlarmPermission()) {
            // Request permission to schedule exact alarms
            requestExactAlarmPermission()
        } else {
            // Set the reminder if permission is granted or not required
            scheduleReminder()
        }
    }

    // Function to check if the exact alarm permission is granted
    private fun hasExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true // If Android version is below 12, no need for permission
        }
    }

    // Function to request the exact alarm permission
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent) // Direct the user to settings to grant permission
        }
    }

    // Function to schedule a reminder
    private fun scheduleReminder() {
        // Intent to trigger the broadcast receiver
        val intent = Intent(this, ReminderBroadcastReceiver::class.java)

        // Use FLAG_IMMUTABLE for API 31 and above
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Get the AlarmManager service
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // Set the alarm to trigger 1 minute from now
        val triggerAtMillis = System.currentTimeMillis() + 60 * 1000 // 1 minute delay
        Log.d("ReminderActivity", "Setting reminder for 1 minute from now")

        // Set the exact alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)

        // Display feedback to the user
        Toast.makeText(this, "Reminder set!", Toast.LENGTH_SHORT).show()
    }
}
