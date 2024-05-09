package com.example.project_mobile_babycare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class signUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        auth = Firebase.auth

        val email: EditText = findViewById(R.id.ETemail)
        val password: EditText = findViewById(R.id.ETpassword)
        val checkPass: EditText = findViewById(R.id.ETcheckPassword)
        val button: Button = findViewById(R.id.BTNsignIn)
        val login: LinearLayout = findViewById(R.id.containerLogIn)

        // Go to log in activity when click on the text
        login.setOnClickListener {
            navigateToLogIn()
            finish()
        }

        // Button sign up event
        button.setOnClickListener {
            val email_ = email.text.toString()
            val password_ = password.text.toString()
            val checkPass_ = checkPass.text.toString()

            // Check if password is the same as checkPass
            if (checkPass_ != password_) {
                Toast.makeText(this, "Hãy kiểm tra lại thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if 3 fields it not empty
            if (email_.isNotEmpty() && password_.isNotEmpty() && checkPass_.isNotEmpty()) {
                // Sign up with email and password
                auth.createUserWithEmailAndPassword(email_, password_)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Send verification mail
                            sendEmailVerification()
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại!\n", Toast.LENGTH_SHORT).show()
                            val errorMessage = task.exception?.message
                            Toast.makeText(baseContext, "$errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToLogIn() {
        val intent = Intent(this, logIn::class.java)
        startActivity(intent)
        finish()
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        if (user != null) {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Đăng ký thành công!\nVui lòng xác nhận mail",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Prevent user automatically sign in to app before verify and sign in
                        auth.signOut()
                        finish()
                        navigateToLogIn()
                    } else {
                        val errorMessage = task.exception?.message
                        Toast.makeText(baseContext, "$errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}