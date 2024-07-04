package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class HeightWeightActivity : AppCompatActivity() {
    lateinit var BTN_back:Button
    lateinit var BTN_add:Button
    lateinit var btn_ssheightweight:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heightweight)
        enableFullscreenMode()

        BTN_back = findViewById(R.id.btn_back)
        BTN_add = findViewById(R.id.btnAdd_heightweight)
        BTN_back.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        BTN_add.setOnClickListener(){
            val intent = Intent(this, Weight_and_Height_Input::class.java)
            startActivity(intent)
        }
        btn_ssheightweight = findViewById(R.id.btn_ssheightweight)
        btn_ssheightweight.setOnClickListener(){
            val intent = Intent(this, HeightWeightWhoTestActivity::class.java)
            startActivity(intent)
        }
    }

    //enable full screen mode
    private fun Activity.enableFullscreenMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Hide the navigation and status bars
        windowInsetsController?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}