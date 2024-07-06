package com.example.project_mobile_babycare

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class weightAndHeightEditActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var btnSave: Button
    private lateinit var btnDel: Button
    private lateinit var edtHeight: EditText
    private lateinit var edtWeight: EditText
    private lateinit var btnDateInput: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_and_height_edit)
        enableFullscreenMode()

        db = Firebase.firestore
        btnSave = findViewById(R.id.btn_save)
        btnDel = findViewById(R.id.btn_del)
        edtHeight = findViewById(R.id.edtCc_hwip)
        edtWeight = findViewById(R.id.edt_cannang)
        btnDateInput = findViewById(R.id.btn_dateinput)

        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")
        val babyWH = intent.getStringExtra("babyWH")

        btnDateInput.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(supportFragmentManager, "datePicker")
        }

        // Ensure userUID, currentBabyUID, and babyWH are not null
        if (userUID != null && currentBabyUID != null && babyWH != null) {
            val babyCollectionPath = db.collection("users").document(userUID)
                .collection("baby").document(currentBabyUID).collection("babyweightheight")
                .document(babyWH)

            babyCollectionPath.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Truy cập vào từng trường (field) trong tài liệu
                        val dateInput = document.getString("dateInput")
                        val height = document.getDouble("height")?.toString()
                        val weight = document.getDouble("weight")?.toString()
                        btnDateInput.text = dateInput
                        edtHeight.setText(height)
                        edtWeight.setText(weight)
                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                }
        }

        btnSave.setOnClickListener {
            val dateInput = btnDateInput.text.toString()
            val height = edtHeight.text.toString()
            val weight = edtWeight.text.toString()

            if (TextUtils.isEmpty(dateInput) || TextUtils.isEmpty(height) || TextUtils.isEmpty(weight)) {
                Toast.makeText(
                    this, "Vui lòng kiểm tra thông tin và thử lại!!!", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (userUID != null && currentBabyUID != null && babyWH != null) {
                val babyCollectionPath = db.collection("users").document(userUID)
                    .collection("baby").document(currentBabyUID)
                    .collection("babyweightheight")
                val docRef = babyCollectionPath.document(babyWH)
                val updates = mapOf(
                    "dateInput" to dateInput,
                    "weight" to weight.toDoubleOrNull(),
                    "height" to height.toDoubleOrNull()
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
                            "Error updating baby weight height: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }

            finish()
        }

        btnDel.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.custom_baby_weightheight_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val btnYes: Button = dialog.findViewById(R.id.btn_delete)
            val btnThoat: Button = dialog.findViewById(R.id.btn_thoat)
            btnYes.setOnClickListener {
                if (userUID != null && currentBabyUID != null && babyWH != null) {
                    db.collection("users").document(userUID)
                        .collection("baby").document(currentBabyUID)
                        .collection("babyweightheight").document(babyWH)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!")
                            Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error deleting document", e)
                        }
                }
                dialog.dismiss()
            }
            btnThoat.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun Activity.enableFullscreenMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
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
            val activity = activity as? weightAndHeightEditActivity
            activity?.btnDateInput?.text = "$day/${month + 1}/$year"
        }
    }
}
