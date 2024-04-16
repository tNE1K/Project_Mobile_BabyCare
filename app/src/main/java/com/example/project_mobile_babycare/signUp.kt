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
        enableEdgeToEdge()

        auth = Firebase.auth

        var email : EditText = findViewById(R.id.ETemail)
        var password : EditText = findViewById(R.id.ETpassword)
        var checkPass : EditText = findViewById(R.id.ETcheckPassword)
        var button : Button = findViewById(R.id.BTNsignIn)
        var login: LinearLayout = findViewById(R.id.containerLogIn)

        login.setOnClickListener {navigateToLogIn()}

        // Button sign up event
        button.setOnClickListener{
            var email_ = email.text.toString()
            var password_ = password.text.toString()
            var checkPass_ = checkPass.text.toString()

            // Check if password is the same as checkPass
            if (checkPass_.toString() != password_.toString())
                Toast.makeText(this, "Hãy kiểm tra lại thông tin!", Toast.LENGTH_SHORT).show()
            else {
                if (email_.isNotEmpty() && password_.isNotEmpty() && checkPass_.isNotEmpty()){
                    // Sign up with email and password
                    auth.createUserWithEmailAndPassword(email_, password_).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Đăng ký thành công. Hãy đăng nhập vào ứng dụng!", Toast.LENGTH_SHORT,).show()
                            val user = auth.currentUser
                            Firebase.auth.signOut()
                            navigateToLogIn()
                            sendEmailVerification()
                        } else {
                            Toast.makeText(this,"Đăng ký thất bại!\n", Toast.LENGTH_SHORT,).show()
                            val errorMessage = task.exception?.message
                            Toast.makeText(baseContext, "$errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else {
                    Toast.makeText(this, "Hãy nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
                    Toast.makeText(this, "Email xác thực đã được gửi đến địa chỉ email của bạn.\nVui lòng kiểm tra hộp thư!", Toast.LENGTH_SHORT).show()
                    navigateToLogIn()
                } else {
                    Toast.makeText(this, "Gửi email xác thực thất bại!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}