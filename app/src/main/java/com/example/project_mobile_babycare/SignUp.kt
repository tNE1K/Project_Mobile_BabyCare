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

class SignUp : AppCompatActivity() {
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

        login.setOnClickListener {
            finish()
        }

        button.setOnClickListener {
            val email_ = email.text.toString()
            val password_ = password.text.toString()
            val checkPass_ = checkPass.text.toString()

            if (email_.isEmpty()) {
                showToastAndSetError(email, "Vui lòng nhập đủ thông tin!")
                return@setOnClickListener
            } else {
                email.setBackgroundResource(R.drawable.rounded_textbox)
            }

            if (password_.isEmpty()) {
                showToastAndSetError(password, "Vui lòng nhập đủ thông tin!")
                return@setOnClickListener
            } else {
                password.setBackgroundResource(R.drawable.rounded_textbox)
            }

            if (checkPass_.isEmpty()) {
                showToastAndSetError(checkPass, "Vui lòng nhập đủ thông tin!")
                return@setOnClickListener
            } else {
                checkPass.setBackgroundResource(R.drawable.rounded_textbox)
            }

            if (password_.length < 8) {
                showToastAndSetError(password, "Vui lòng nhập mật khẩu có trên 8 ký tự!")
                return@setOnClickListener
            } else {
                password.setBackgroundResource(R.drawable.rounded_textbox)
            }

            if (checkPass_ != password_) {
                showToastAndSetError(password, "Hãy kiểm tra lại thông tin!")
                showToastAndSetError(checkPass, "Hãy kiểm tra lại thông tin!")
                return@setOnClickListener
            } else {
                password.setBackgroundResource(R.drawable.rounded_textbox)
                checkPass.setBackgroundResource(R.drawable.rounded_textbox)
            }

            if (email_.isNotEmpty() && password_.isNotEmpty() && checkPass_.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email_, password_)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            sendEmailVerification()
                        } else {
                            val errorMessage = task.exception?.message
                            Toast.makeText(this, "Đăng ký thất bại!\n$errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Đăng ký thành công!\nVui lòng xác nhận mail",
                        Toast.LENGTH_SHORT
                    ).show()
                    auth.signOut()
                    finish()
                } else {
                    val errorMessage = task.exception?.message
                    Toast.makeText(baseContext, "$errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showToastAndSetError(editText: EditText, message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        editText.setBackgroundResource(R.drawable.error_edittext)
    }
}
