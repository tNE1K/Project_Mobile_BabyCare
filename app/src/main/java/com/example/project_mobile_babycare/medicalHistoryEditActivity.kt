package com.example.project_mobile_babycare

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.widget.Button
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class medicalHistoryEditActivity : AppCompatActivity() {
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history_edit)
        enableFullscreenMode()

        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        val btnStartDate: Button = findViewById(R.id.button_start_date)
        val btnEndDate: Button = findViewById(R.id.button_end_date)
        val etDiseaseName: EditText = findViewById(R.id.edit_text_disease_name)
        val etSymptom: EditText = findViewById(R.id.edit_text_symptoms)
        val btnDelete: Button = findViewById(R.id.btn_delete)
        val btnSave: Button = findViewById(R.id.btn_save)

        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")
        val medicalUID = intent.getStringExtra("medicalUID")

        btnStartDate.setOnClickListener {
            val newFragment = DatePickerFragmentStart()
            newFragment.show(supportFragmentManager, "datePicker")
        }

        btnEndDate.setOnClickListener {
            val newFragment = DatePickerFragmentEnd()
            newFragment.show(supportFragmentManager, "datePicker")
        }

        // Ensure userUID and currentBabyUID and medicalUID are not null
        if (userUID != null && currentBabyUID != null && medicalUID != null) {
            val babyCollectionPath = db.collection("users").document(userUID)
                .collection("baby").document(currentBabyUID).collection("babyMedicalHistory")
                .document(medicalUID)


            babyCollectionPath.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Truy cập vào từng trường (field) trong tài liệu
                        val startDate = document.getString("startDate")
                        val endDate = document.getString("endDate")
                        val symptom = document.getString("symptom")
                        val diseaseName = document.getString("diseaseName")
                        btnStartDate.setText(startDate)
                        btnEndDate.setText(endDate)
                        etDiseaseName.setText(diseaseName)
                        etSymptom.setText(symptom)

                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                }
        }

        btnSave.setOnClickListener {
            if (TextUtils.isEmpty(btnStartDate.text) || TextUtils.isEmpty(btnEndDate.text) || TextUtils.isEmpty(
                    etDiseaseName.text
                ) || TextUtils.isEmpty(etSymptom.text)
            ) {
                Toast.makeText(
                    this, "Vui lòng kiểm tra thông tin và thử lại!!!", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {
                if (user != null) {
                    val babyCollectionPath = db.collection("users").document(userUID!!)
                        .collection("baby").document(currentBabyUID!!)
                        .collection("babyMedicalHistory")

                    val docRef = babyCollectionPath.document(medicalUID!!)
                    val startDate = btnStartDate.text.toString()
                    val endDate = btnEndDate.text.toString()
                    val symptom = etSymptom.text.toString()
                    val diseaseName = etDiseaseName.text.toString()
                    val updates = mapOf(
                        "startDate" to startDate,
                        "endDate" to endDate,
                        "symptom" to symptom,
                        "diseaseName" to diseaseName
                    )

                    docRef.update(updates)
                        .addOnSuccessListener {
                            // Document updated successfully
                            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener { e ->
                            // Handle the error
                            Toast.makeText(
                                this,
                                "Error updating baby medical history: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

            }
            finish()
        }

        btnDelete.setOnClickListener{
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.custom_delete_medical_history_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val btnYes: Button = dialog.findViewById(R.id.btn_delete)
            val btnThoat: Button = dialog.findViewById(R.id.btn_thoat)
            btnYes.setOnClickListener {
                db.collection("users").document(user!!.uid)
                    .collection("baby").document(currentBabyUID!!).collection("babyMedicalHistory").document(medicalUID!!)
                    .delete()
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
                dialog.dismiss()
                Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
            btnThoat.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
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
            val activity = activity as? medicalHistoryEditActivity
            activity?.findViewById<Button>(R.id.button_end_date)?.text = "$day/${month + 1}/$year"
        }
    }
}