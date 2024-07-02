package com.example.project_mobile_babycare

import android.app.Activity
import android.app.Dialog
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

    override fun onCreate(savedInstanceState: Bundle?) {
        db = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableFullscreenMode()

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
                    if (babyName != null) {
                        babyList.add(Baby(babyName))
                    }
                }
                babyList.add(Baby(""))
                babyAdapter.notifyDataSetChanged()
            }
        }

        hSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                // Handle item selection
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no item selected
            }
        }

        btnBabyInfo.setOnClickListener {
            val intent = Intent(this, BabyInfo::class.java)
            startActivity(intent)
            finish()
        }
        btnBabyWnH.setOnClickListener {
            val intent = Intent(this, HeightWeightActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnBabyMedicalHistory.setOnClickListener {
            val intent = Intent(this, MedicalHistoryView::class.java)
            startActivity(intent)
            finish()
        }
        btnBabyInjection.setOnClickListener {
            val intent = Intent(this, BabyInjecActivity::class.java)
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
        windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
