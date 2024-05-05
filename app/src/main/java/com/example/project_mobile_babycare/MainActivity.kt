package com.example.project_mobile_babycare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var btnBabyInfo:Button
    lateinit var btnBabyWnH:Button
    lateinit var btnBabyMedicalHistory:Button
    lateinit var btnBabyInjection:Button
    lateinit var btnBabyNutrition:Button
    lateinit var btnBabyMemory:Button
    lateinit var btnBabyMilestone:Button
    lateinit var btnLogout:Button


    lateinit var hSpinner: Spinner
    lateinit var baby:Baby
    lateinit var babyAdapter:BabyAdapter
    lateinit var babyList:ArrayList<Baby>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnBabyInfo = findViewById(R.id.BTNbabyInfo)
        btnBabyWnH = findViewById(R.id.BTNbabyWH)
        btnBabyMedicalHistory = findViewById(R.id.BTNbabyMedicalHistory)
        btnBabyInjection = findViewById(R.id.BTNbabyInjection)
        btnBabyNutrition = findViewById(R.id.BTNbabyNutrition)
        btnBabyMemory = findViewById(R.id.BTNbabyMemory)
        btnBabyMilestone = findViewById(R.id.BTNbabyMilestone)
        btnLogout = findViewById(R.id.BTNlogout)

        babyList = ArrayList<Baby>()
        babyList.add(Baby("Kiên"))
        babyList.add(Baby("Bảo"))
        babyList.add(Baby("Nam"))
        babyList.add(Baby(""))
        hSpinner = findViewById(R.id.SPNlistBaby)
        babyAdapter = BabyAdapter(this, babyList)
        hSpinner.adapter = babyAdapter
        hSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        btnBabyInfo.setOnClickListener{
            val intent = Intent(this, Baby_Info::class.java)
            startActivity(intent)
        }

        btnBabyWnH.setOnClickListener{
            val intent = Intent(this, heightweightActivity::class.java)
            startActivity(intent)
        }

        btnBabyMedicalHistory.setOnClickListener{
            val intent = Intent(this, medical_history_view::class.java)
            startActivity(intent)
        }
//        btnBabyInjection.setOnClickListener{
//            val intent = Intent(this, heightweightActivity::class.java)
//            startActivity(intent)
//        }
        btnBabyNutrition.setOnClickListener{
            val intent = Intent(this, Baby_nutrition_age::class.java)
            startActivity(intent)
        }
//        btnBabyMemory.setOnClickListener{
//            val intent = Intent(this, heightweightActivity::class.java)
//            startActivity(intent)
//        }
//        btnBabyMilestone.setOnClickListener{
//            val intent = Intent(this, heightweightActivity::class.java)
//            startActivity(intent)
//        }
//        btnLogout.setOnClickListener{
//            val intent = Intent(this, heightweightActivity::class.java)
//            startActivity(intent)
//        }

    }
}