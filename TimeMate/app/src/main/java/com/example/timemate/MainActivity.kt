package com.example.timemate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private val CHANNEL_ID = "reminder_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        // Handle navigation item clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            handleNavigation(menuItem.itemId)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Create the notification channel for reminders
        createNotificationChannel()

        // Request permissions
        requestNotificationPermission()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkExactAlarmPermission()
        }

        // Load TaskListFragment by default
        if (savedInstanceState == null) {
            replaceFragment(TaskListFragment()) // Load TaskListFragment initially
        }
    }

    private fun handleNavigation(itemId: Int) {
        when (itemId) {
            R.id.nav_tasks -> replaceFragment(TaskListFragment())
            R.id.nav_timer -> replaceFragment(TimerFragment())
            R.id.nav_notifications -> replaceFragment(ReminderFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
        Log.d("MainActivity", "Fragment replaced with ${fragment::class.java.simpleName}")
    }

    // Handle back press to close the sidebar if open
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Function to create the notification channel for reminders
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for task reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Request notification permission on Android 13+
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    // Function to check if exact alarm permission is granted
    private fun isExactAlarmPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permission = "android.permission.SCHEDULE_EXACT_ALARM"
            val granted = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                Log.w("MainActivity", "Exact alarm permission is not granted.")
            }
            granted
        } else {
            true
        }
    }

    // Function to prompt user to enable exact alarm permission
    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isExactAlarmPermissionGranted()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    // Inflate the menu with three dots
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.drawer_menu, menu)
        return true
    }

    // Handle clicks on the toolbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_tasks, R.id.nav_timer, R.id.nav_notifications -> {
                handleNavigation(item.itemId)
                true
            }
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
