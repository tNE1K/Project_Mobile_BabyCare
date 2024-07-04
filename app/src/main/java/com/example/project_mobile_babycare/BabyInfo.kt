package com.example.project_mobile_babycare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
        var month: Int

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
                    var babyCount: Int
                    val name = ETname.text.toString()
                    val height = ETheight.text.toString().toInt()
                    val weight = ETweight.text.toString().toInt()
                    val birth = BTNdateOfBirth.text.toString()
                    val babyData = hashMapOf(
                        "name" to name,
                        "birth" to birth,
                        "height" to height,
                        "weight" to weight,
                        "male" to Male.isChecked,
                        "female" to Female.isChecked
                    )
                    val basicBabyData = hashMapOf(
                        "babyName" to name,
                        "test" to "test"
                    )
//                    reference to the collection baby within the document corresponding
//                    to the current user (user.uid) in the users collection
                    val babyCollectionPath =
                        db.collection("users").document(user.uid).collection("baby")
//                    reference to a specific document within the baby collection identified by the baby's name
                    val baby = babyCollectionPath.document(name)
//                    set the basic data for the document identified by the baby's name
//                    if the document does not exist, it will be created
                    baby.set(basicBabyData)
//                    adds the detailed baby data as new document in babyList collection
                    val babyInfo = baby.collection("babyInfo")
//                    add baby info
                    babyInfo.add(babyData)

//                    back to main activity
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