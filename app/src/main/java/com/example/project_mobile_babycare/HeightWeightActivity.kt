package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class HeightWeightActivity : AppCompatActivity() {
    lateinit var BTN_back: Button
    lateinit var BTN_add: Button
    lateinit var btn_ssheightweight: Button
    lateinit var lvHeightWeight: ListView
    lateinit var heightWeightAdapter: HeightWeightAdapter
    lateinit var heightWeightList: ArrayList<HeightWeight>
    val auth = Firebase.auth
    val user = auth.currentUser
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heightweight)
        enableFullscreenMode()


        BTN_back = findViewById(R.id.btn_back)
        BTN_add = findViewById(R.id.btnAdd_heightweight)
        btn_ssheightweight = findViewById(R.id.btn_ssheightweight)

        heightWeightList = ArrayList()
        lvHeightWeight = findViewById(R.id.lv_heightWeight)
        heightWeightAdapter = HeightWeightAdapter(this, heightWeightList)
        lvHeightWeight.adapter = heightWeightAdapter

        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")

        loadHeightWeightData(userUID, currentBabyUID)

        BTN_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        BTN_add.setOnClickListener {
            val intent = Intent(this, Weight_and_Height_Input::class.java)
            intent.putExtra("userUID", user?.uid)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }
        btn_ssheightweight.setOnClickListener {
            val intent = Intent(this, HeightWeightWhoTestActivity::class.java)
            intent.putExtra("userUID", userUID)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }


    }

    private fun loadHeightWeightData(userUID: String?, babyUID: String?) {
        if (userUID != null && babyUID != null) {
            val docRef = db.collection("users").document(userUID)
                .collection("baby").document(babyUID)
                .collection("babyweightheight")

            docRef.get().addOnSuccessListener { result ->
                heightWeightList.clear()
                val tempList = ArrayList<HeightWeight>()
                for (document in result) {
                    val dateInput = document.getString("dateInput")
                    val height = document.getLong("height").toString()
                    val weight = document.getDouble("weight").toString()
                    if (dateInput != null && height != null && weight != null) {
                        tempList.add(HeightWeight(dateInput, "$height/$weight"))
                        Log.d("HeightWeightData", "dateInput: $dateInput, height: $height, weight: $weight")
                    }
                }

                // Sắp xếp tempList theo ngày nhập
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                tempList.sortBy { dateFormat.parse(it.start) }

                // Cập nhật heightWeightList
                heightWeightList.addAll(tempList)
                heightWeightAdapter.notifyDataSetChanged()
            }.addOnFailureListener { exception ->
                Log.d("HeightWeightData", "Error getting documents: ", exception)
            }
        } else {
            Log.d("HeightWeightData", "userUID or babyUID is null")
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
}