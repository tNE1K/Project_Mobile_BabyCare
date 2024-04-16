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
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in on start of app
        var currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMainActivity()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)
        enableEdgeToEdge()
        var button: Button = findViewById(R.id.BTNsignIn)
        var username: EditText = findViewById(R.id.ETname)
        var password: EditText = findViewById(R.id.ETpassword)
        var signup: LinearLayout = findViewById(R.id.containerSignUp)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // If user already signed in, take user to main activity
        }

//    Go to sign up activity
        signup.setOnClickListener {navigateToSignUp()}

        button.setOnClickListener {
            val username_ = username.text.toString()
            val password_ = password.text.toString()

            if (username_.isNotEmpty() && password_.isNotEmpty()) {
                // Sign in with email and password
                auth.signInWithEmailAndPassword(username_, password_).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        var user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            // Email is verified, allow the user to log in
                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                            navigateToMainActivity()
                        } else {
                            user = auth.currentUser
                            if (user != null && !user.isEmailVerified) {
                                user.sendEmailVerification()
                                user.sendEmailVerification().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Liên kết xác thực trước đó đã hết hạn!\nLiên kết xác thực mới đã được gửi đến địa chỉ email của bạn!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val errorMessage = task.exception?.message
                                        Toast.makeText(baseContext, "Failed to send verification email:\n$errorMessage", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            Firebase.auth.signOut()
                        }
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(this, "Đăng nhập thất bại, hãy kiểm tra lại thông tin!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // If email or password is empty, display a message to the user.
                Toast.makeText(this, "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun navigateToMainActivity() {
//         Start the main activity or any other appropriate activity
//         For example:
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToSignUp() {
//         Start the main activity or any other appropriate activity
//         For example:
         val intent = Intent(this, signUp::class.java)
         startActivity(intent)
    }

    private fun navigateToLogIn() {
//         Start the main activity or any other appropriate activity
//         For example:
        val intent = Intent(this, logIn::class.java)
        startActivity(intent)
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email xác thực đã được gửi đến địa chỉ email của bạn. Vui lòng kiểm tra hộp thư đến và xác thực email trước khi đăng nhập!", Toast.LENGTH_SHORT).show()
                    navigateToLogIn()
                } else {
                    Toast.makeText(this, "Gửi email xác thực thất bại!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
