package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.common.base.Objects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LogIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var email: String
    private lateinit var password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)
        enableFullscreenMode()

        auth = Firebase.auth
        val buttonSignIn: Button = findViewById(R.id.BTNsignIn)
        val username: EditText = findViewById(R.id.ETname)
        val password: EditText = findViewById(R.id.ETpassword)
        val signup: LinearLayout = findViewById(R.id.containerSignUp)
        signup.setOnClickListener {
            navigateToSignUp()
        }
        buttonSignIn.setOnClickListener {
            email = username.text.toString()
            this.password = password.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                username.setBackgroundResource(R.drawable.error_edittext)
                return@setOnClickListener
            } else {
                username.setBackgroundResource(R.drawable.rounded_textbox)
            }

            if (this.password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                password.setBackgroundResource(R.drawable.error_edittext)
                return@setOnClickListener
            } else {
                password.setBackgroundResource(R.drawable.rounded_textbox)
            }

            if (email.isNotEmpty() && this.password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, this.password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            checkUserVerified()
                        } else {
                            Toast.makeText(
                                this,
                                "Đăng nhập thất bại\nHãy kiểm tra lại thông tin!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkUserLoggedIn()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    private fun checkUserLoggedIn() {
        auth.currentUser
        if (auth.currentUser != null) {
            checkUserVerified()
        }
    }

    private fun checkUserVerified() {
        val user = auth.currentUser
        if (user?.isEmailVerified == true) {
            navigateToMainActivity()
        } else {
            reSendEmailVerification()
        }
    }

    private fun reSendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email xác thực đã được gửi lại!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Vui lòng kiểm tra hộp thư của bạn!", Toast.LENGTH_SHORT)
                        .show()
                }
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
