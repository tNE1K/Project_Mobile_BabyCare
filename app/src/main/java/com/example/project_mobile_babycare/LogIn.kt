package com.example.project_mobile_babycare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogIn : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)
        auth = Firebase.auth
        val buttonSignIn: Button = findViewById(R.id.BTNsignIn)
        val username: EditText = findViewById(R.id.ETname)
        val password: EditText = findViewById(R.id.ETpassword)
        val signup: LinearLayout = findViewById(R.id.containerSignUp)

        signup.setOnClickListener {
            navigateToSignUp()
        }

        buttonSignIn.setOnClickListener {
            val username_ = username.text.toString()
            val password_ = password.text.toString()

            if (username_.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                username.setBackgroundResource(R.drawable.error_edittext)
                return@setOnClickListener
            } else {
                username.setBackgroundResource(R.drawable.rounded_textbox)
            }
            if (password_.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                password.setBackgroundResource(R.drawable.error_edittext)
                return@setOnClickListener
            } else {
                password.setBackgroundResource(R.drawable.rounded_textbox)
            }

            if (username_.isNotEmpty() && password_.isNotEmpty()) {
                auth.signInWithEmailAndPassword(username_, password_)
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
//            checkUserExistence {
//                navigateToMainActivity()
//            }
            checkUserVerified()
        }
    }
//    private fun checkUserExistence(onComplete: () -> Unit) {
//        val currentUser = auth.currentUser
//        Firebase.auth.fetchSignInMethodsForEmail(currentUser?.email ?: "")
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful && task.result?.signInMethods?.isNotEmpty() == true) {
//                    onComplete()
//                } else {
//                    Firebase.auth.signOut()
//                }
//            }
//    }

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
}
