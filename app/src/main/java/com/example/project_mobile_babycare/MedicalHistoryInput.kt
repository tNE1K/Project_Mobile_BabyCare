package com.example.project_mobile_babycare

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible

class MedicalHistoryInput : AppCompatActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medical_history_input)
        enableFullscreenMode()
        var BTNstartdate: Button = findViewById(R.id.button_start_date)
        var StartCalendarContainer: FrameLayout = findViewById(R.id.startCalendarContainer)
        var StartDatePick: DatePicker = findViewById(R.id.startDatePicker)

        var BTNenddate: Button = findViewById(R.id.button_end_date)
        var EndCalendarContainer: FrameLayout = findViewById(R.id.endCalendarContainer)
        var EndDatePick: DatePicker = findViewById(R.id.endDatePicker)

        var month: Int
        BTNstartdate.setOnClickListener {
            if (EndCalendarContainer.isVisible)
                EndCalendarContainer.visibility = View.GONE

            if (!StartCalendarContainer.isVisible)
                StartCalendarContainer.visibility = View.VISIBLE
            else StartCalendarContainer.visibility = View.GONE
        }

        StartDatePick.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            month = monthOfYear + 1
            val msg = "$dayOfMonth/$month/$year"
            BTNstartdate.text = msg
        }

        BTNenddate.setOnClickListener {
            if (StartCalendarContainer.isVisible)
                StartCalendarContainer.visibility = View.GONE

            if (!EndCalendarContainer.isVisible)
                EndCalendarContainer.visibility = View.VISIBLE
            else EndCalendarContainer.visibility = View.GONE
        }

        EndDatePick.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            month = monthOfYear + 1
            val msg = "$dayOfMonth/$month/$year"
            BTNenddate.text = msg
        }
    }

    //hide system bar
    private fun Activity.enableFullscreenMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Hide the navigation and status bars
        windowInsetsController.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}