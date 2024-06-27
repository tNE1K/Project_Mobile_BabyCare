package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MedicalHistoryView : AppCompatActivity() {

    lateinit var lvMedical: ListView
    lateinit var medical: Medical
    lateinit var medicalAdapter: MedicalAdapter
    lateinit var medicalList: ArrayList<Medical>
    lateinit var BTN_back:Button
    lateinit var BTN_add:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history_view)
        enableFullscreenMode()

        BTN_back = findViewById(R.id.btn_back)
        BTN_add = findViewById(R.id.btn_save)

        medicalList = ArrayList<Medical>()
        medicalList.add(Medical("1/1/2022", "1/2/2022"))
        medicalList.add(Medical("1/4/2022", "1/5/2022"))
        medicalList.add(Medical("1/6/2022", "1/7/2022"))
        medicalList.add(Medical("1/8/2022", "1/9/2022"))
        lvMedical = findViewById(R.id.lv_medical_history)
        medicalAdapter = MedicalAdapter(this, medicalList)
        lvMedical.adapter = medicalAdapter

        BTN_back.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        BTN_add.setOnClickListener(){
            val intent = Intent(this, MedicalHistoryInput::class.java)
            startActivity(intent)
        }

    }

    //enable full screen mode
    private fun Activity.enableFullscreenMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Hide the navigation and status bars
        windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

}