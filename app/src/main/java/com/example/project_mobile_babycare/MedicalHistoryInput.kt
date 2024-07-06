package com.example.project_mobile_babycare

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class MedicalHistoryInput : AppCompatActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history_input)
        enableFullscreenMode()

        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore
        val btnAdd: Button = findViewById(R.id.btn_add)
        val btnBack: Button = findViewById(R.id.btn_back_medical)
        val btnStartDate: Button = findViewById(R.id.button_start_date)
        val btnEndDate: Button = findViewById(R.id.button_end_date)
        val etDiseaseName: EditText = findViewById(R.id.edit_text_disease_name)
        val etSymptoms: EditText = findViewById(R.id.edit_text_symptoms)

        btnStartDate.setOnClickListener {
            val newFragment = DatePickerFragmentStart()
            newFragment.show(supportFragmentManager, "datePicker")
        }

        btnEndDate.setOnClickListener {
            val newFragment = DatePickerFragmentEnd()
            newFragment.show(supportFragmentManager, "datePicker")
        }
        //Get text from Intent
        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")

        btnAdd.setOnClickListener{
            if (TextUtils.isEmpty(btnStartDate.text) || TextUtils.isEmpty(btnEndDate.text) || TextUtils.isEmpty(etDiseaseName.text) || TextUtils.isEmpty(etSymptoms.text)
            ) {
                Toast.makeText(
                    this, "Vui lòng kiểm tra thông tin và thử lại!!!", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                if (user != null) {
                    val startDate = btnStartDate.text.toString()
                    val endDate = btnEndDate.text.toString()
                    val diseaseName = etDiseaseName.text.toString()
                    val symptom = etSymptoms.text.toString()
                    val medicalHistoryData = hashMapOf(
                        "startDate" to startDate,
                        "endDate" to endDate,
                        "diseaseName" to diseaseName,
                        "symptom" to symptom
                    )

                    val babyCollectionPath =
                        db.collection("users").document(userUID!!).collection("baby").document(currentBabyUID!!)

                    val babyMedicalHistory = babyCollectionPath.collection("babyMedicalHistory")
//                    add baby medical history
                    babyMedicalHistory.add(medicalHistoryData)

                    Toast.makeText(this, "Thêm thông tin thành công!", Toast.LENGTH_SHORT).show()
                }
            }
            val back = Intent(this, MedicalHistoryView::class.java)
            back.putExtra("userUID", userUID)
            back.putExtra("babyUID", currentBabyUID)
            startActivity(back)
            finish()
        }

        btnBack.setOnClickListener {
            val back = Intent(this, MedicalHistoryView::class.java)
            back.putExtra("userUID", userUID)
            back.putExtra("babyUID", currentBabyUID)
            startActivity(back)
            finish()
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

    class DatePickerFragmentStart : DialogFragment(), DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker.
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            val activity = activity as? MedicalHistoryInput
            activity?.findViewById<Button>(R.id.button_start_date)?.text = "$day/${month + 1}/$year"
        }
    }

    class DatePickerFragmentEnd : DialogFragment(), DatePickerDialog.OnDateSetListener {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker.
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            val activity = activity as? MedicalHistoryInput
            activity?.findViewById<Button>(R.id.button_end_date)?.text = "$day/${month + 1}/$year"
        }
    }
}