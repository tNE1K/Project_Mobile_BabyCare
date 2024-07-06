package com.example.project_mobile_babycare

import android.app.Activity
import android.app.Dialog
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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

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
    lateinit var babyAdapter: BabyAdapter
    lateinit var babyList: ArrayList<Baby>
    lateinit var builder: AlertDialog.Builder
    lateinit var db: FirebaseFirestore
    lateinit var currentBabyUID: String
    lateinit var day: TextView
    lateinit var month: TextView
    lateinit var year: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        db = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableFullscreenMode()

        day = findViewById(R.id.TVshowDays)
        month = findViewById(R.id.TVshowMonths)
        year = findViewById(R.id.TVshowYears)
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
        hSpinner = findViewById(R.id.SPNlistBaby)
        babyAdapter = BabyAdapter(this, babyList)
        hSpinner.adapter = babyAdapter

        user?.let {
            val userPath = db.collection("users").document(user.uid).collection("baby")
            userPath.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MainActivity", "listen:error", e)
                    return@addSnapshotListener
                }
                babyList.clear()
                for (doc in snapshots!!) {
                    val babyName = doc.getString("babyName")
                    val babyID = doc.getString("babyUID")
                    if (babyName != null && babyID != null) {
                        babyList.add(Baby(babyName, babyID))
                    }
                }
                babyList.add(Baby("", ""))
                babyAdapter.notifyDataSetChanged()
            }
        }

        hSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                // Handle item selection
                currentBabyUID = babyList.get(position).uid
                if (currentBabyUID == "") {
                    return
                }
                Log.d(TAG, currentBabyUID)
                user?.let {
                    val babyInfoPath = db.collection("users").document(user.uid)
                        .collection("baby").document(currentBabyUID)
                        .collection("babyInfo")
                    babyInfoPath.addSnapshotListener { snapshot, e ->
                        for (doc in snapshot!!) {
                            val dateStr = doc.getString("birth").toString()
                            val (dayStr, monthStr, yearStr) = dateStr.split("/")
                            val day_ = dayStr.toInt()
                            val month_ = monthStr.toInt()
                            val year_ = yearStr.toInt()

                            // Calculate age
                            val birthDate = Calendar.getInstance()
                            birthDate.set(
                                year_,
                                month_ - 1,
                                day_
                            ) // month is zero-based in Calendar

                            val currentDate = Calendar.getInstance()

                            var years =
                                currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
                            var months =
                                currentDate.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH)
                            var days =
                                currentDate.get(Calendar.DAY_OF_MONTH) - birthDate.get(Calendar.DAY_OF_MONTH)

                            // Adjust negative days
                            if (days < 0) {
                                months--
                                val copyBirthDate = birthDate.clone() as Calendar
                                copyBirthDate.add(Calendar.MONTH, 1)
                                days += copyBirthDate.getActualMaximum(Calendar.DAY_OF_MONTH)
                            }

                            // Adjust negative months
                            if (months < 0) {
                                years--
                                months += 12
                            }

                            // Update your UI or log the result
                            Log.d(TAG, "Age: $years years, $months months, $days days")

                            // Example of updating EditText fields
                            day.setText(days.toString())
                            month.setText(months.toString())
                            year.setText(years.toString())
                        }
                    }
                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no item selected
                currentBabyUID = babyList.get(0).uid
                user?.let {
                    val userPath = db.collection("users").document(user.uid).collection("baby")
                    val babyPath = userPath.document(currentBabyUID)
                    Log.d(TAG, babyPath.get().toString())
                }
            }
        }

        btnBabyInfo.setOnClickListener {
            if (currentBabyUID == "") {
                Toast.makeText(this, "Bạn chưa em bé để xem thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, BabyInfo::class.java)
            intent.putExtra("userUID", user?.uid)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }
        btnBabyWnH.setOnClickListener {
            val intent = Intent(this, HeightWeightActivity::class.java)
            intent.putExtra("userUID", user?.uid)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }
        btnBabyMedicalHistory.setOnClickListener {
            val intent = Intent(this, MedicalHistoryView::class.java)
            intent.putExtra("userUID", user?.uid)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }
        btnBabyInjection.setOnClickListener {
            val intent = Intent(this, BabyInjecActivity::class.java)
            intent.putExtra("userUID", user?.uid)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }
        btnBabyNutrition.setOnClickListener {
            val intent = Intent(this, BabyNutritionAge::class.java)
            startActivity(intent)
            finish()
        }
        btnBabyMemory.setOnClickListener {
            val intent = Intent(this, BabyMemoryView::class.java)
            intent.putExtra("userUID", user?.uid)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }
        btnBabyMilestone.setOnClickListener {
            val intent = Intent(this, BabyMilestone::class.java)
            startActivity(intent)
            finish()
        }
        btnLogout.setOnClickListener {
            showCustomDialogBox(auth)
        }
    }

    private fun showCustomDialogBox(auth: FirebaseAuth) {
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

    // Enable full screen mode
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
