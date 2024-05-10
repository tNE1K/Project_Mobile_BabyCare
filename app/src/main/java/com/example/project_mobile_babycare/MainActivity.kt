package com.example.project_mobile_babycare

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.example.project_mobile_babycare.signOut

class MainActivity : AppCompatActivity() {
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
    lateinit var builder:AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // listen for auth state change
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // User is signed out, navigate to login screen
                val intent = Intent(this, logIn::class.java)
                startActivity(intent)
                finish() // Finish MainActivity
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
            val intent = Intent(this, Baby_Info::class.java)
            startActivity(intent)
        }

        btnBabyWnH.setOnClickListener {
            val intent = Intent(this, heightweightActivity::class.java)
            startActivity(intent)
        }

        btnBabyMedicalHistory.setOnClickListener {
            val intent = Intent(this, medical_history_view::class.java)
            startActivity(intent)
        }
        btnBabyInjection.setOnClickListener{
            val intent = Intent(this, BabyInjecActivity::class.java)
            startActivity(intent)
        }
        btnBabyNutrition.setOnClickListener {
            val intent = Intent(this, Baby_nutrition_age::class.java)
            startActivity(intent)
        }
        btnBabyMemory.setOnClickListener{
            val intent = Intent(this, Baby_Memory_View::class.java)
            startActivity(intent)
        }
        btnBabyMilestone.setOnClickListener{
            val intent = Intent(this, BabyMilestone::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            showCustomDialogBox()

        }
    }

    private fun showCustomDialogBox() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_logout_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnYes:Button = dialog.findViewById(R.id.btn_logout)
        val btnThoat:Button = dialog.findViewById(R.id.btn_thoat)
        btnYes.setOnClickListener{
            signOut()
            val intent = Intent(this, logIn::class.java)
            startActivity(intent)
            finish()
        }
        btnThoat.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
}