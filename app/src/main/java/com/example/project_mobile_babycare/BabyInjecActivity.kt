package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BabyInjecActivity : AppCompatActivity() {
    lateinit var btnBack: Button
    lateinit var btnSave: Button
    lateinit var listviewInjection: ListView
    lateinit var injection: Injection
    lateinit var injectionList: ArrayList<Injection>
    lateinit var injectionAdapter: InjectionAdapter
    lateinit var name:String
    var isInjected:Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baby_injec)
        enableFullscreenMode()

        injectionList = ArrayList<Injection>()
        btnBack = findViewById(R.id.btnUndo_bbinjec)
        btnSave = findViewById(R.id.btnSave_bbinjec)

        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        //Get text from Intent
        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")

        if (userUID != null && currentBabyUID != null) {
            val babyCollectionPath = db.collection("users").document(userUID)
                .collection("baby").document(currentBabyUID).collection("babyInjection")


            babyCollectionPath.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MainActivity", "listen:error", e)
                    return@addSnapshotListener
                }
                injectionList.clear()
                for (doc in snapshots!!) {
                    name = doc.getString("name")!!
                    if (doc.getBoolean("isInjected") == true) {
                        isInjected = true

                    } else {
                        isInjected = false
                    }
                    if (name != null) {
                        injectionList.add(Injection(name, isInjected))
                    }
                }
                injectionAdapter.notifyDataSetChanged()
            }
        }


        listviewInjection = findViewById(R.id.lvbbinjec)
        injectionAdapter = InjectionAdapter(this, injectionList)
        listviewInjection.adapter = injectionAdapter



        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnSave.setOnClickListener {
            injectionAdapter.saveStatesToFirestore()

    }



}

    //enable full screen mode
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