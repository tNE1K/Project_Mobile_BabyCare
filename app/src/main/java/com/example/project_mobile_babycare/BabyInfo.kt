package com.example.project_mobile_babycare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.util.Date

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
        var month: Int

        BTNdateOfBirth.setOnClickListener() {
            if (!CalendarContainer.isVisible)
                CalendarContainer.visibility = VISIBLE
            else CalendarContainer.visibility = GONE
        }

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
                    this,
                    "Vui lòng kiểm tra thông tin và thử lại!!!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            } else {
                if (user != null) {
                    var babyCount: Int
                    val height_ = ETheight.text.toString().toInt()
                    val weight_ = ETweight.text.toString().toInt()
                    val data = hashMapOf(
                        "name" to ETname.text.toString(),
                        "birth" to BTNdateOfBirth.text.toString(),
                        "height" to height_,
                        "weight" to weight_,
                        "male" to Male.isChecked,
                        "female" to Female.isChecked
                    )
                    val userUid = db.collection("users").document(user.uid)
                    userUid.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
//                                update babycount
                                babyCount = document.getLong("babyCount")?.toInt()!! + 1
                                userUid.update("babyCount", babyCount)
                                Log.d(ContentValues.TAG, "Increased babyCount to $babyCount")
                            } else {
                                Log.d(ContentValues.TAG, "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(ContentValues.TAG, "Get failed with ", exception)
                        }

//                    add new baby, baby info and first milestone
                    val babyId = userUid.collection("baby").document(ETname.text.toString())
                    babyId.collection("Info").document().set(data)
                        .addOnSuccessListener {
                            Log.d("Add baby info"," successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Add baby info", "error with:", e)
                        }
                    val current = LocalDateTime.now()
                    val milestoneData = hashMapOf(
                        "height" to height_,
                        "weight" to weight_
                    )
                    babyId.collection("Milestone").document(current.toString()).set(milestoneData)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
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