package com.example.timemate

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.*

class ReminderFragment : Fragment() {

    private var timeSet = false
    private var selectedTimeInMillis: Long = 0
    private lateinit var reminderMessageEditText: EditText
    private lateinit var displayReminderTextView: TextView // To display the reminder message

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_remider, container, false)

        // Initialize views
        val setTimeButton: Button = view.findViewById(R.id.set_time_button)
        val setReminderButton: Button = view.findViewById(R.id.set_reminder_button)
        reminderMessageEditText = view.findViewById(R.id.reminder_message)
        displayReminderTextView = view.findViewById(R.id.display_reminder_text_view) // Initialize the TextView

        // Set time button listener
        setTimeButton.setOnClickListener {
            showTimePicker()
        }

        // Set reminder button listener
        setReminderButton.setOnClickListener {
            setReminder()
        }

        return view
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Set the selected time in milliseconds
                selectedTimeInMillis = calendar.timeInMillis
                timeSet = true

                Log.d("ReminderFragment", "Time set to: ${calendar.time}")
                Toast.makeText(context, "Time set: ${calendar.time}", Toast.LENGTH_SHORT).show()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun setReminder() {
        if (!timeSet) {
            Toast.makeText(context, "Please set a time first!", Toast.LENGTH_SHORT).show()
            Log.w("ReminderFragment", "No time set. Reminder not scheduled.")
            return
        }

        val message = reminderMessageEditText.text.toString()
        if (message.isEmpty()) {
            Toast.makeText(context, "Please enter a reminder message!", Toast.LENGTH_SHORT).show()
            Log.w("ReminderFragment", "No message set. Reminder not scheduled.")
            return
        }

        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Check if the app can schedule exact alarms for API 31 and above
                if (alarmManager.canScheduleExactAlarms()) {
                    scheduleExactAlarm(alarmManager, pendingIntent)
                } else {
                    Toast.makeText(context, "Exact alarm permission not granted", Toast.LENGTH_SHORT).show()
                    Log.e("ReminderFragment", "Exact alarm permission not granted")
                }
            } else {
                // For devices below API 31, schedule the alarm
                scheduleExactAlarm(alarmManager, pendingIntent)
            }
        } catch (e: SecurityException) {
            // Handle the security exception if the app doesn't have permission
            Toast.makeText(context, "Failed to set reminder due to permissions", Toast.LENGTH_SHORT).show()
            Log.e("ReminderFragment", "SecurityException: ${e.message}")
        }

        // Update the TextView to display the reminder
        displayReminderTextView.text = "Reminder set: $message at ${Date(selectedTimeInMillis)}"
    }

    private fun scheduleExactAlarm(alarmManager: AlarmManager, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For API 23 and above, use setExactAndAllowWhileIdle
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                selectedTimeInMillis,
                pendingIntent
            )
        } else {
            // For lower API levels, use setExact
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                selectedTimeInMillis,
                pendingIntent
            )
        }

        Toast.makeText(context, "Reminder set successfully!", Toast.LENGTH_SHORT).show()
        Log.d("ReminderFragment", "Reminder set for: ${Date(selectedTimeInMillis)} with message: ${reminderMessageEditText.text}")
    }
}
