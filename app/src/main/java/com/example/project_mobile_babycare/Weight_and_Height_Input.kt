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

class Weight_and_Height_Input : AppCompatActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_and_height_input)
        enableFullscreenMode()
        var BTNdateInput: Button = findViewById(R.id.btn_dateinput)
        var CalendarContainer: FrameLayout = findViewById(R.id.calendarContainer)
        var DatePick: DatePicker = findViewById(R.id.datePicker)
        var month: Int
        BTNdateInput.setOnClickListener {
            if (!CalendarContainer.isVisible)
                CalendarContainer.visibility = View.VISIBLE
            else CalendarContainer.visibility = View.GONE
        }

        DatePick.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            month = monthOfYear + 1
            val msg = "$dayOfMonth/$month/$year"
            BTNdateInput.text = msg
        }
    }

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