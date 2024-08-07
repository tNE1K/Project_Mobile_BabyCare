package com.example.project_mobile_babycare

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.GridView
import android.widget.ListView
import android.widget.Toast
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

        gvMemory.setOnItemClickListener{ parent, view, position, id ->

            // Show a toast with the clicked item value
            userPath.get()
                .addOnSuccessListener{ documents ->
                    // Kiểm tra nếu vị trí mong muốn nằm trong phạm vi của danh sách kết quả
                    if (position in 0 until documents.size()) {
                        // Lấy tài liệu ở vị trí mong muốn
                        val targetDocument = documents.documents[position]

                        // Lấy UID từ tài liệu
                        val documentId = targetDocument.id
                        Log.d("Firestore", "Document ID at position $position: $documentId")
                        val intent = Intent(this, MemoryAlbumEdit::class.java)
                        intent.putExtra("userUID", userUID)
                        intent.putExtra("babyUID", currentBabyUID)
                        intent.putExtra("memoryUID", documentId)
                        startActivity(intent)
                        finish()

//                        // Xử lý dữ liệu khác từ tài liệu nếu cần
//                        val data = targetDocument.data
//                        Log.d("Firestore", "Document Data: $data")
                    } else {
                        Log.d("Firestore", "Position $position is out of bounds")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "Error getting documents: ", exception)
                }

        }
        gvMemory.setOnItemLongClickListener { parent, view, position, id ->
            // Tạo Dialog để xác nhận xóa
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.custom_delete_memory_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val btnYes: Button = dialog.findViewById(R.id.btn_delete)
            val btnThoat: Button = dialog.findViewById(R.id.btn_thoat)

            // Lấy tài liệu từ vị trí tương ứng trong danh sách
            userPath.get()
                .addOnSuccessListener { documents ->
                    if (position in 0 until documents.size()) {
                        val targetDocument = documents.documents[position]
                        val documentId = targetDocument.id

                        btnYes.setOnClickListener {
                            // Xóa tài liệu từ Firestore
                            userPath.document(documentId)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d("Firestore", "DocumentSnapshot successfully deleted!")
                                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error deleting document", e)
                                    Toast.makeText(this, "Có lỗi xảy ra khi xóa!", Toast.LENGTH_SHORT).show()
                                }
                            dialog.dismiss()
                        }

                        btnThoat.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()
                    } else {
                        Log.d("Firestore", "Position $position is out of bounds")
                        Toast.makeText(this, "Không thể xóa mục này!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "Error getting documents: ", exception)
                    Toast.makeText(this, "Có lỗi xảy ra khi lấy tài liệu!", Toast.LENGTH_SHORT).show()
                }

            true // Trả về true để biểu thị sự kiện đã được xử lý
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