package com.example.project_mobile_babycare

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

class Weight_and_Height_Input : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button
    private lateinit var edtHeight: EditText
    private lateinit var edtWeight: EditText
    private lateinit var btnDateInput: Button
    private var userUID: String? = null
    private var babyUID: String? = null
    private var babyInfoUID: String? = null
    lateinit var auth: FirebaseAuth
    private var birthDate: LocalDate? = null  // Biến lưu trữ ngày sinh

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        db = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_and_height_input)
        enableFullscreenMode()

        // Initialize Firebase Firestore
        db = Firebase.firestore

        btnSave = findViewById(R.id.btnSave_hwip)
        btnBack = findViewById(R.id.btnBack_hwip)
        edtHeight = findViewById(R.id.edtCc_hwip)
        edtWeight = findViewById(R.id.edt_cannang)
        btnDateInput = findViewById(R.id.btn_dateinput)

        // Get userUID and babyUID from Intent
        userUID = intent.getStringExtra("userUID")
        babyUID = intent.getStringExtra("babyUID")

        btnDateInput.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(supportFragmentManager, "datePicker")
        }

        btnSave.setOnClickListener {
            saveDataToFirebase()
        }
        btnBack.setOnClickListener {
            val back = Intent(this, HeightWeightActivity::class.java)
            back.putExtra("userUID", userUID)
            back.putExtra("babyUID", babyUID)
            startActivity(back)
            finish()
        }

        loadBabyInfo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadBabyInfo() {
        if (userUID.isNullOrEmpty() || babyUID.isNullOrEmpty()) {
            Toast.makeText(this, "User or baby information missing", Toast.LENGTH_SHORT).show()
            return
        }

        val babyInfor = db.collection("users").document(userUID!!)
            .collection("baby").document(babyUID!!)
            .collection("babyInfo")

        babyInfor.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents[0]
                    babyInfoUID = document.id
                    birthDate = document.getString("birth")?.let { formatDateString(it) }?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy")) }
                    Log.d("Firestore", "BabyInfo Document ID: $babyInfoUID")
                    Log.d("Firestore", "Baby birthDate: $birthDate")
                } else {
                    Toast.makeText(this, "No babyInfo document found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting babyInfo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDataToFirebase() {
        val heightStr = edtHeight.text.toString()
        val weightStr = edtWeight.text.toString()
        val dateInput = btnDateInput.text.toString()

        if (heightStr.isEmpty() || weightStr.isEmpty() || dateInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val height = heightStr.toLongOrNull()
        val weight = weightStr.toDoubleOrNull()

        if (height == null || weight == null) {
            Toast.makeText(this, "Invalid height or weight", Toast.LENGTH_SHORT).show()
            return
        }

        if (userUID.isNullOrEmpty() || babyUID.isNullOrEmpty()) {
            Toast.makeText(this, "User or baby information missing", Toast.LENGTH_SHORT).show()
            return
        }

        val babyInfor = db.collection("users").document(userUID!!)
            .collection("baby").document(babyUID!!)
            .collection("babyInfo")

        babyInfor.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val dobString = querySnapshot.documents[0].getString("birth")
                    val monthsOld = calculateMonthsOld(dobString, dateInput)

                    val formattedDateInput = formatDateString(dateInput)
                    val monthYear = getMonthYear(formattedDateInput)

                    db.collection("users").document(userUID!!)
                        .collection("baby").document(babyUID!!)
                        .collection("babyweightheight")
                        .whereEqualTo("monthYear", monthYear)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val babyWeightHeightData = hashMapOf(
                                "height" to height,
                                "weight" to weight,
                                "dateInput" to formattedDateInput,
                                "monthsOld" to monthsOld,
                                "monthYear" to monthYear
                            )

                            if (querySnapshot.documents.isNotEmpty()) {
                                // If the document exists, update it
                                val documentId = querySnapshot.documents[0].id
                                db.collection("users").document(userUID!!)
                                    .collection("baby").document(babyUID!!)
                                    .collection("babyweightheight")
                                    .document(documentId)
                                    .set(babyWeightHeightData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Data updated successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Error updating data: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                // If the document does not exist, add a new one
                                db.collection("users").document(userUID!!)
                                    .collection("baby").document(babyUID!!)
                                    .collection("babyweightheight")
                                    .add(babyWeightHeightData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Data saved successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Error saving data: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                } else {
                    Toast.makeText(this, "No such babyInfo document", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting babyInfo: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        val back = Intent(this, HeightWeightActivity::class.java)
        back.putExtra("userUID", userUID)
        back.putExtra("babyUID", babyUID)
        startActivity(back)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDateString(dateString: String): String {
        val parts = dateString.split("/")
        val day = if (parts[0].length == 1) "0" + parts[0] else parts[0]
        val month = if (parts[1].length == 1) "0" + parts[1] else parts[1]
        val year = parts[2]
        return "$day/$month/$year"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateMonthsOld(dobString: String?, dateInput: String): Long {
        if (dobString == null) return 0

        val formattedDobString = formatDateString(dobString)
        val formattedDateInput = formatDateString(dateInput)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dob = LocalDate.parse(formattedDobString, formatter)
        val inputDate = LocalDate.parse(formattedDateInput, formatter)

        // Tính số tháng giữa dob và inputDate
        val monthsBetween = ChronoUnit.MONTHS.between(dob.withDayOfMonth(1), inputDate.withDayOfMonth(1))

        return monthsBetween
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMonthYear(dateString: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = LocalDate.parse(dateString, formatter)
        return "${date.monthValue}/${date.year}"
    }

    private fun Activity.enableFullscreenMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Hide the navigation and status bars
        windowInsetsController?.let {
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

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            val activity = activity as? Weight_and_Height_Input
            val selectedDate = LocalDate.of(year, month + 1, day)

            if (activity?.birthDate != null && selectedDate.isBefore(activity.birthDate)) {
                Toast.makeText(activity, "The selected date cannot be before the birth date.", Toast.LENGTH_SHORT).show()
            } else {
                activity?.findViewById<Button>(R.id.btn_dateinput)?.text = "${day.toString().padStart(2, '0')}/${(month + 1).toString().padStart(2, '0')}/$year"
            }
        }
    }
}
