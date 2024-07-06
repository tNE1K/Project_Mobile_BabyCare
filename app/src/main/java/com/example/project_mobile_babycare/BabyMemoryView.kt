package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BabyMemoryView : AppCompatActivity() {

    lateinit var gvMemory: GridView
    lateinit var memory: Memory
    lateinit var memoryAdapter: MemoryAdapter
    lateinit var memoryList: ArrayList<Memory>
    lateinit var BTN_back: Button
    lateinit var BTN_add: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baby_memory_view)
        enableFullscreenMode()

        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore


        BTN_back = findViewById(R.id.btn_back)
        BTN_add = findViewById(R.id.btn_save)

        //Get text from Intent
        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")

        memoryList = ArrayList<Memory>()

        gvMemory = findViewById(R.id.gv_baby_memory)
        memoryAdapter = MemoryAdapter(this, memoryList)
        gvMemory.adapter = memoryAdapter

        val userPath = db.collection("users").document(userUID!!).collection("baby").document(currentBabyUID!!).collection("babyMemory")


        user?.let {
            userPath.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("MainActivity", "listen:error", e)
                    return@addSnapshotListener
                }
                memoryList.clear()
                for (doc in snapshots!!) {
                    val imageUrl = doc.getString("imageUrl")
                    val note = doc.getString("note")
                    if (imageUrl != null && note != null) {
                        memoryList.add(Memory(imageUrl, note))
                    }
                }
                memoryAdapter.notifyDataSetChanged()
            }
        }

        BTN_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        BTN_add.setOnClickListener {
            val intent = Intent(this, MemoryAlbumAdd::class.java)
            intent.putExtra("userUID", userUID)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
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