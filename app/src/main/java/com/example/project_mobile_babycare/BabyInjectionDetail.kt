package com.example.project_mobile_babycare

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class BabyInjectionDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFullscreenMode()
        setContentView(R.layout.activity_baby_injection_detail)

        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")
        val injectionUID = intent.getStringExtra("injectionUID")

        val etInjectionName: EditText = findViewById(R.id.etInjectionName)
        val etInjectionPlace: EditText = findViewById(R.id.etInjectionPlace)
        val btnInjectionDate:Button = findViewById(R.id.btnInjectionDate)
        val cb_datiem: CheckBox = findViewById(R.id.cb_datiem)
        val btnBack:Button = findViewById(R.id.btnBack)
        val btnSave:Button = findViewById(R.id.btnSave)

        btnInjectionDate.setOnClickListener{

                val newFragment = BabyInjectionDetail.DatePickerFragment()
                newFragment.show(supportFragmentManager, "datePicker")

        }


        // Ensure userUID and currentBabyUID and medicalUID are not null
        if (userUID != null && currentBabyUID != null && injectionUID != null) {
            val babyCollectionPath = db.collection("users").document(userUID)
                .collection("baby").document(currentBabyUID).collection("babyInjection")
                .document(injectionUID)


            babyCollectionPath.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Truy cập vào từng trường (field) trong tài liệu
                        val name = document.getString("name")
                        val status = document.getBoolean("isInjected")
                        val place = document.getString("injectionPlace")
                        val date = document.getString("injectionDate")
                        etInjectionName.setText(name)
                        cb_datiem.isChecked = status!!
                        etInjectionPlace.setText(place)
                        btnInjectionDate.setText(date)

                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                }
        }

        btnBack.setOnClickListener{
            val back = Intent(this, BabyInjecActivity::class.java)
            back.putExtra("userUID", userUID)
            back.putExtra("babyUID", currentBabyUID)
            startActivity(back)
            finish()
        }

        btnSave.setOnClickListener{
            if (TextUtils.isEmpty(etInjectionPlace.text) || TextUtils.isEmpty(btnInjectionDate.text)) {
                Toast.makeText(
                    this, "Vui lòng kiểm tra thông tin và thử lại!!!", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                if (user != null) {
                    val babyCollectionPath = db.collection("users").document(userUID!!)
                        .collection("baby").document(currentBabyUID!!).collection("babyInjection").document(injectionUID!!)

                    val injectionPlace = etInjectionPlace.text.toString()
                    val injectionDate = btnInjectionDate.text.toString()
                    val isInjected = cb_datiem.isChecked
                    val data = hashMapOf(
                        "injectionPlace" to injectionPlace,
                        "injectionDate" to injectionDate,
                        "isInjected" to isInjected
                    )

                    babyCollectionPath.set(data, SetOptions.merge())
                        .addOnSuccessListener {
                            // Document updated successfully
                            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener { e ->
                            // Handle the error
                            Toast.makeText(
                                this,
                                "Error updating baby info: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    val back = Intent(this, BabyInjecActivity::class.java)
                    back.putExtra("userUID", userUID)
                    back.putExtra("babyUID", currentBabyUID)
                    startActivity(back)
                    finish()
                }
            }
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

    class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker.
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            val activity = activity as? BabyInjectionDetail
            activity?.findViewById<Button>(R.id.btnInjectionDate)?.text = "$day/${month + 1}/$year"
        }
    }
}