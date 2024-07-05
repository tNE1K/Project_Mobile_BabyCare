package com.example.project_mobile_babycare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BabyInfo : AppCompatActivity() {


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.baby_infor)
        enableFullscreenMode()

        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        var BTNdateOfBirth: Button = findViewById(R.id.btn_dateofbirth)
        var CalendarContainer: FrameLayout = findViewById(R.id.calendarContainer)
        var DatePick: DatePicker = findViewById(R.id.datePicker)
        var ETname: EditText = findViewById(R.id.edt_babyname)
        var ETheight: EditText = findViewById(R.id.edt_chieucao)
        var ETweight: EditText = findViewById(R.id.edt_cannang)
        var Male: RadioButton = findViewById(R.id.rbt_male)
        var Female: RadioButton = findViewById(R.id.rbt_female)
        var BTNsave: Button = findViewById(R.id.btn_infsave)
        var BTNback: Button = findViewById(R.id.btn_infback)
        var scrollView: ScrollView = findViewById(R.id.sv_body)
        var month: Int


        //Get text from Intent
        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")
        // Ensure userUID and currentBabyUID are not null
        if (userUID != null && currentBabyUID != null) {
            val babyCollectionPath = db.collection("users").document(userUID)
                .collection("baby").document(currentBabyUID).collection("babyInfo")


            babyCollectionPath.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MainActivity", "listen:error", e)
                    return@addSnapshotListener
                }
                for (doc in snapshots!!) {
                    ETname.setText(doc.getString("name"))
                    BTNdateOfBirth.setText(doc.getString("birth"))
                    ETheight.setText(doc.getLong("height").toString())
                    ETweight.setText(doc.getDouble("weight").toString())
                    if(doc.getBoolean("male") == true){
                        Male.isChecked = true
                        Female.isChecked = false
                    }
                    else
                    {
                        Male.isChecked = false
                        Female.isChecked = true
                    }
                }
            }

        }

        fun getCurrentDate(): String {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            return currentDate.format(formatter)
        }

        BTNdateOfBirth.setOnClickListener() {
            if (!CalendarContainer.isVisible) CalendarContainer.visibility = VISIBLE
            else CalendarContainer.visibility = GONE
        }

        val currentDate = getCurrentDate()

        DatePick.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            month = monthOfYear + 1
            val msg = "$dayOfMonth/$month/$year"
            BTNdateOfBirth.setText(msg)
        }

        BTNsave.setOnClickListener() {
            if (TextUtils.isEmpty(ETname.text) || TextUtils.isEmpty(ETheight.text) || TextUtils.isEmpty(
                    ETweight.text
                ) || (!Male.isChecked && !Female.isChecked)
            ) {
                Toast.makeText(
                    this, "Vui lòng kiểm tra thông tin và thử lại!!!", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                if (user != null) {
                    val babyCollectionPath = db.collection("users").document(userUID!!)
                        .collection("baby").document(currentBabyUID!!).collection("babyInfo")
                    babyCollectionPath.get().addOnSuccessListener { result ->
                        for (doc in result) {
                            val babyInfoUID = doc.id // Initialize babyInfoUID here

                            // Now you can safely use babyInfoUID
                            val docRef = babyCollectionPath.document(babyInfoUID)
                            val name = ETname.text.toString()
                            val height = ETheight.text.toString().toLong()
                            val weight = ETweight.text.toString().toDouble()
                            val birth = BTNdateOfBirth.text.toString()
                            val updates = mapOf(
                                "name" to name,
                                "birth" to birth,
                                "height" to height,
                                "weight" to weight,
                                "male" to Male.isChecked,
                                "female" to Female.isChecked
                            )

                            docRef.update(updates)
                                .addOnSuccessListener {
                                    // Document updated successfully
                                    Toast.makeText(this, "Baby info updated", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    // Handle the error
                                    Toast.makeText(this, "Error updating baby info: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }


                    val babyBasicPath = db.collection("users").document(userUID!!).collection("baby")
                    babyBasicPath.get().addOnSuccessListener { result ->
                        for (doc in result) {
                            val docRef = babyBasicPath.document(currentBabyUID!!)
                            val name = ETname.text.toString()
                            val updates = mapOf(
                                "babyName" to name
                            )

                            docRef.update(updates)
                                .addOnSuccessListener {
                                    // Document updated successfully
                                }
                                .addOnFailureListener { e ->
                                    // Handle the error
                                    Toast.makeText(this, "Error updating baby info: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
        }
        BTNback.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    //enable full screen mode
    private fun Activity.enableFullscreenMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Hide the navigation and status bars
        windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}