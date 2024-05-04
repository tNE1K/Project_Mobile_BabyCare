package com.example.project_mobile_babycare

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class medical_history_view : AppCompatActivity() {

    lateinit var lvMedical: ListView
    lateinit var medical: Medical
    lateinit var medicalAdapter: MedicalAdapter
    lateinit var medicalList: ArrayList<Medical>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        medicalList = ArrayList<Medical>()
        medicalList.add(Medical("1/1/2022", "1/2/2022"))
        medicalList.add(Medical("1/4/2022", "1/5/2022"))
        medicalList.add(Medical("1/6/2022", "1/7/2022"))
        medicalList.add(Medical("1/8/2022", "1/9/2022"))
        lvMedical = findViewById(R.id.lv_medical_history)
        medicalAdapter = MedicalAdapter(this, medicalList)
        lvMedical.adapter = medicalAdapter

    }
}