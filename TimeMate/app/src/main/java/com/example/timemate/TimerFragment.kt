package com.example.timemate

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class TimerFragment : Fragment() {
    private lateinit var timerText: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var startTime = 0L
    private var elapsedTime = 0L
    private var isRunning = false

    private val UPDATE_INTERVAL = 1000L // 1 second

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        timerText = view.findViewById(R.id.timer_text)
        startButton = view.findViewById(R.id.start_button)
        stopButton = view.findViewById(R.id.stop_button)
        resetButton = view.findViewById(R.id.reset_button)

        handler = Handler()

        startButton.setOnClickListener { startTimer() }
        stopButton.setOnClickListener { stopTimer() }
        resetButton.setOnClickListener { resetTimer() }

        return view
    }

    private fun startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis()
            runnable = object : Runnable {
                override fun run() {
                    elapsedTime = System.currentTimeMillis() - startTime
                    val seconds = (elapsedTime / 1000) % 60
                    val minutes = (elapsedTime / (1000 * 60)) % 60
                    val hours = (elapsedTime / (1000 * 60 * 60)) % 24

                    timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    handler?.postDelayed(this, UPDATE_INTERVAL)
                }
            }
            handler?.postDelayed(runnable!!, UPDATE_INTERVAL)
            isRunning = true
        }
    }

    private fun stopTimer() {
        if (isRunning) {
            handler?.removeCallbacks(runnable!!)
            isRunning = false
        }
    }

    private fun resetTimer() {
        stopTimer()
        elapsedTime = 0L
        timerText.text = "00:00:00"
    }
}
