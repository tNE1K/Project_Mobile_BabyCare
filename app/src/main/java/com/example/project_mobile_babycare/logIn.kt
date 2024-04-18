package com.example.project_mobile_babycare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class logIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)
        enableEdgeToEdge()
        auth = Firebase.auth

        checkUserLogedIn()

        val button: Button = findViewById(R.id.BTNsignIn)
        val username: EditText = findViewById(R.id.ETname)
        val password: EditText = findViewById(R.id.ETpassword)
        val signup: LinearLayout = findViewById(R.id.containerSignUp)

        // Go to sign up activity
        signup.setOnClickListener {
            navigateToSignUp()
        }

        button.setOnClickListener {
            val username_ = username.text.toString()
            val password_ = password.text.toString()
            if (username_.isNotEmpty() && password_.isNotEmpty()) {
                // Sign in with email and password
                auth.signInWithEmailAndPassword(username_, password_).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        checkUserVerified()
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại\nHãy kiểm tra lại thông tin!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    public fun navigateToSignUp() {
         val intent = Intent(this, signUp::class.java)
         startActivity(intent)
    }

//    Check user is loged in or not
    public fun checkUserLogedIn() {
        // Check if user is signed in on start of app
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMainActivity()
            finish()
        }
    }

    public fun checkUserVerified() {
        val user = auth.currentUser
        if (user?.isEmailVerified == true){
            navigateToMainActivity()
            finish()
        }
        else{
            reSendEmailVerification()
        }
    }

    public fun reSendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email xác thực đã được gửi lại!", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                } else {
                    val errorMessage = task.exception?.message
                    Toast.makeText(baseContext, "$errorMessage", Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
            }
    }
}
