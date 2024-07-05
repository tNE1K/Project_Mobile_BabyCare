package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MedicalHistoryView : AppCompatActivity() {

    lateinit var lvMedical: ListView
    lateinit var medical: Medical
    lateinit var medicalAdapter: MedicalAdapter
    lateinit var medicalList: ArrayList<Medical>
    lateinit var BTN_back: Button
    lateinit var BTN_add: Button
    val auth = Firebase.auth
    val user = auth.currentUser
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history_view)
        enableFullscreenMode()

        BTN_back = findViewById(R.id.btn_back)
        BTN_add = findViewById(R.id.btn_save)

        medicalList = ArrayList<Medical>()

        lvMedical = findViewById(R.id.lv_medical_history)
        medicalAdapter = MedicalAdapter(this, medicalList)
        lvMedical.adapter = medicalAdapter

        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")

        user?.let {
            val userPath = db.collection("users").document(userUID!!).collection("baby").document(currentBabyUID!!).collection("babyMedicalHistory")
            userPath.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MainActivity", "listen:error", e)
                    return@addSnapshotListener
                }
                medicalList.clear()
                for (doc in snapshots!!) {
                    val startDate = doc.getString("startDate")
                    val diseaseName = doc.getString("diseaseName")
                    if (startDate != null && diseaseName != null) {
                        medicalList.add(Medical(startDate, diseaseName))
                    }
                }
                medicalAdapter.notifyDataSetChanged()
            }
        }


        BTN_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        BTN_add.setOnClickListener {
            val intent = Intent(this, MedicalHistoryInput::class.java)
            intent.putExtra("userUID", userUID)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }

    }

    //enable full screen mode
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