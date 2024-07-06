package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MemoryAlbumAdd : AppCompatActivity() {
    lateinit var btn_back: Button
    lateinit var btn_add: Button
    lateinit var note: EditText
    private lateinit var imageView: ImageView
    private val REQUEST_CAMERA = 1
    private val REQUEST_GALLERY = 2

    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFullscreenMode()
        setContentView(R.layout.activity_memory_album_add)

        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")

        storageReference = FirebaseStorage.getInstance().reference

        var selectedImage: Uri? = null
        note = findViewById(R.id.note)
        val btn_media: Button = findViewById(R.id.btn_addMedia)
        btn_add = findViewById(R.id.btn_add)
        imageView = findViewById(R.id.image_view)

        btn_media.setOnClickListener {
            val options = arrayOf("Camera", "Gallery")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose an option")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            builder.show()
        }

        btn_add.setOnClickListener {
            if (note.text.toString().isEmpty()) {
                Toast.makeText(
                    this@MemoryAlbumAdd,
                    "Hãy chắc chắn rằng bạn đã thêm hình ảnh hoặc ghi chú rồi nhé",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            // Check if image is selected from Camera or Gallery
            if (selectedImage != null) {
                uploadImageToFirebase(selectedImage)
            } else if (imageView.drawable != null) {
                // Handle image captured from camera (data might be available directly)
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                uploadImageToFirebase(bitmap)
            } else {
                // No image selected, show error message
                Toast.makeText(
                    this@MemoryAlbumAdd,
                    "Hãy chắc chắn rằng bạn đã thêm hình ảnh hoặc ghi chú rồi nhé!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return@setOnClickListener
        }
        btn_back = findViewById(R.id.btn_back)
        btn_back.setOnClickListener {
            val intent = Intent(this, BabyMemoryView::class.java)
            intent.putExtra("userUID", userUID)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }

    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var selectedImage: Uri? = null
        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    val photo: Bitmap = data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(photo)
                    imageView.setBackgroundColor(0)
                }

                REQUEST_GALLERY -> {
                    selectedImage = data?.data!!
                    imageView.setImageURI(selectedImage)
                    imageView.setBackgroundColor(0)
                }
            }
        }
        btn_add.setOnClickListener {
            if (note.text.toString().isEmpty()) {
                Toast.makeText(
                    this@MemoryAlbumAdd,
                    "Hãy chắc chắn rằng bạn đã thêm hình ảnh hoặc ghi chú rồi nhé",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            // Check if image is selected from Camera or Gallery
            if (selectedImage != null) {
                uploadImageToFirebase(selectedImage)
            } else if (imageView.drawable != null) {
                // Handle image captured from camera (data might be available directly)
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                uploadImageToFirebase(bitmap)
            } else {
                // No image selected, show error message
                Toast.makeText(
                    this@MemoryAlbumAdd,
                    "Hãy chắc chắn rằng bạn đã thêm hình ảnh hoặc ghi chú rồi nhé",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val intent = Intent(this, BabyMemoryView::class.java)
            intent.putExtra("userUID", userUID)
            intent.putExtra("babyUID", currentBabyUID)
            startActivity(intent)
            finish()
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        uploadToFirebase(data)
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val data = inputStream?.readBytes()
        if (data != null) {
            uploadToFirebase(data)
        }
    }

    private fun uploadToFirebase(data: ByteArray) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timestamp.jpg"
        val fileRef = storageReference.child("images/$fileName")

        val metadata = StorageMetadata.Builder()
            .setCustomMetadata("uploadDate", timestamp)
            .build()

        val uploadTask = fileRef.putBytes(data, metadata)
        uploadTask.addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                // Lấy URL của ảnh đã tải lên
                val downloadUrl = uri.toString()
                saveNoteToFirestore(downloadUrl)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNoteToFirestore(imageUrl: String) {
        //Get text from Intent
        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val currentBabyUID = intent.getStringExtra("babyUID")

        val db = Firebase.firestore
        val noteText = note.text.toString()
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val noteData = hashMapOf(
            "imageUrl" to imageUrl,
            "note" to noteText,
            "timestamp" to timestamp
        )

        db.collection("users").document(userUID!!)
            .collection("baby").document(currentBabyUID!!).collection("babyMemory")
            .add(noteData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Thêm kỷ niệm thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving note: ${e.message}", Toast.LENGTH_SHORT).show()
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