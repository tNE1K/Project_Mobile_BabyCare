package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MemoryAlbumEdit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFullscreenMode()
        setContentView(R.layout.activity_memory_album_edit)

        var imageView: ImageView = findViewById(R.id.image_view)
        var et_note: EditText = findViewById(R.id.note)
        var btn_save: Button = findViewById(R.id.btn_save)
        var btn_back: Button = findViewById(R.id.btn_back)

        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")
        val memoryUID = intent.getStringExtra("memoryUID")

        if (userUID != null && currentBabyUID != null && memoryUID != null) {
            val babyCollectionPath = db.collection("users").document(userUID)
                .collection("baby").document(currentBabyUID).collection("babyMemory")
                .document(memoryUID)


            babyCollectionPath.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Truy cập vào từng trường (field) trong tài liệu
                        val imageUrl = document.getString("imageUrl")
                        val note = document.getString("note")
                        Glide.with(this)
                            .load(imageUrl)
                            .into(imageView)
                        et_note.setText(note)

                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                }
        }
        btn_back.setOnClickListener {
            val intent = Intent(this, BabyMemoryView::class.java)
            intent.putExtra("userUID", userUID)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }
        btn_save.setOnClickListener{
            if (et_note.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Hãy chắc chắn rằng bạn đã thêm hình ảnh hoặc ghi chú rồi nhé",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            else{
                if (user != null) {
                    val babyCollectionPath = db.collection("users").document(userUID!!)
                        .collection("baby").document(currentBabyUID!!)
                        .collection("babyMemory")

                    val docRef = babyCollectionPath.document(memoryUID!!)
                    val note = et_note.text.toString()
                    val updates = mapOf(
                        "note" to note
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
            val intent = Intent(this, BabyMemoryView::class.java)
            intent.putExtra("userUID", userUID)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }
    }



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