package com.example.project_mobile_babycare

import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var btnBabyInfo: Button
    lateinit var btnBabyWnH: Button
    lateinit var btnBabyMedicalHistory: Button
    lateinit var btnBabyInjection: Button
    lateinit var btnBabyNutrition: Button
    lateinit var btnBabyMemory: Button
    lateinit var btnBabyMilestone: Button
    lateinit var btnLogout: Button
    lateinit var hSpinner: Spinner
    lateinit var baby: Baby
    lateinit var babyAdapter: BabyAdapter
    lateinit var babyList: ArrayList<Baby>
    lateinit var builder: AlertDialog.Builder
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        val user = auth.currentUser

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (user != null) {
            val docRef = db.collection("users").document(user.uid)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val babyCount = document.getLong("babyCount")?.toInt()
                        if (babyCount == 0) {
                            val intent = Intent(this, BabyInfo::class.java)
                            startActivity(intent)
                        } else {
                            Log.d(TAG, "BabyCount field is not found")
                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }

        btnBabyInfo = findViewById(R.id.BTNbabyInfo)
        btnBabyWnH = findViewById(R.id.BTNbabyWH)
        btnBabyMedicalHistory = findViewById(R.id.BTNbabyMedicalHistory)
        btnBabyInjection = findViewById(R.id.BTNbabyInjection)
        btnBabyNutrition = findViewById(R.id.BTNbabyNutrition)
        btnBabyMemory = findViewById(R.id.BTNbabyMemory)
        btnBabyMilestone = findViewById(R.id.BTNbabyMilestone)
        btnLogout = findViewById(R.id.BTNlogout)
        builder = AlertDialog.Builder(this)
        babyList = ArrayList<Baby>()
        babyList.add(Baby("Kiên"))
        babyList.add(Baby("Bảo"))
        babyList.add(Baby("Nam"))
        babyList.add(Baby(""))
        hSpinner = findViewById(R.id.SPNlistBaby)
        babyAdapter = BabyAdapter(this, babyList)
        hSpinner.adapter = babyAdapter
        hSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        btnBabyInfo.setOnClickListener {
            val intent = Intent(this, BabyInfo::class.java)
            startActivity(intent)
        }

        btnBabyWnH.setOnClickListener {
            val intent = Intent(this, HeightWeightActivity::class.java)
            startActivity(intent)
        }

        btnBabyMedicalHistory.setOnClickListener {
            val intent = Intent(this, MedicalHistoryView::class.java)
            startActivity(intent)
        }
        btnBabyInjection.setOnClickListener {
            val intent = Intent(this, BabyInjecActivity::class.java)
            startActivity(intent)
        }
        btnBabyNutrition.setOnClickListener {
            val intent = Intent(this, BabyNutritionAge::class.java)
            startActivity(intent)
        }
        btnBabyMemory.setOnClickListener {
            val intent = Intent(this, BabyMemoryView::class.java)
            startActivity(intent)
        }
        btnBabyMilestone.setOnClickListener {
            val intent = Intent(this, BabyMilestone::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            showCustomDialogBox(auth)
        }
    }

    private fun showCustomDialogBox(auth : FirebaseAuth) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_logout_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnYes: Button = dialog.findViewById(R.id.btn_logout)
        val btnThoat: Button = dialog.findViewById(R.id.btn_thoat)
        btnYes.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        }
        btnThoat.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}