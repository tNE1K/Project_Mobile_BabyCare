package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class BabyNutritionWeight : AppCompatActivity() {

    lateinit var BTN_back: Button
    lateinit var BTN_tinh_theo_tuoi: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFullscreenMode()
        setContentView(R.layout.activity_baby_nutrition_weight)


        BTN_back = findViewById(R.id.btn_back)
        BTN_tinh_theo_tuoi = findViewById(R.id.btn_tinhTheoTuoi)
        BTN_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        BTN_tinh_theo_tuoi.setOnClickListener {
            val intent = Intent(this, BabyNutritionAge::class.java)
            startActivity(intent)
            finish()
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