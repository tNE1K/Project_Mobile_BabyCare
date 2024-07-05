package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class BabyMilestone : AppCompatActivity() {

    lateinit var BTN_back: Button
    lateinit var lvMilestone: ListView
    lateinit var milestone: Milestone
    lateinit var milestoneAdapter: MilestoneAdapter
    lateinit var milestoneList: ArrayList<Milestone>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baby_milestone)
        enableFullscreenMode()

        BTN_back = findViewById(R.id.btn_back)

        milestoneList = ArrayList<Milestone>()
        milestoneList.add(Milestone(R.drawable.babyhead, "Nâng đầu lên"))
        milestoneList.add(Milestone(R.drawable.babysound, "Phản xạ với âm thanh"))
        milestoneList.add(Milestone(R.drawable.babyscroll, "Lật"))
        milestoneList.add(Milestone(R.drawable.babysit, "Ngồi"))
        milestoneList.add(Milestone(R.drawable.babycrawl, "Trườn, bò"))
        milestoneList.add(Milestone(R.drawable.babystand, "Đứng"))
        milestoneList.add(Milestone(R.drawable.babytooth, "Mọc răng"))
        milestoneList.add(Milestone(R.drawable.babybrain, "Nhận thức"))
        milestoneList.add(Milestone(R.drawable.babyfeet, "Quá trình tập đi"))
        lvMilestone = findViewById(R.id.lv_baby_milestone)
        milestoneAdapter = MilestoneAdapter(this, milestoneList)
        lvMilestone.adapter = milestoneAdapter

        lvMilestone.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                when (position) {
                    0 -> {
                        val intent = Intent(this, Milestone_info_head::class.java)
                        startActivity(intent)
                        finish()
                    }

                    1 -> {
                        val intent = Intent(this, Milestone_info_sound::class.java)
                        startActivity(intent)
                        finish()
                    }

                    2 -> {
                        val intent = Intent(this, Milestone_info_scroll::class.java)
                        startActivity(intent)
                        finish()
                    }

                    3 -> {
                        val intent = Intent(this, Milestone_info_sit::class.java)
                        startActivity(intent)
                        finish()
                    }

                    4 -> {
                        val intent = Intent(this, Milestone_info_crawl::class.java)
                        startActivity(intent)
                        finish()
                    }

                    5 -> {
                        val intent = Intent(this, Milestone_info_stand::class.java)
                        startActivity(intent)
                        finish()
                    }

                    6 -> {
                        val intent = Intent(this, Milestone_info_tooth::class.java)
                        startActivity(intent)
                        finish()
                    }

                    7 -> {
                        val intent = Intent(this, Milestone_info_knowledge::class.java)
                        startActivity(intent)
                        finish()
                    }

                    8 -> {
                        val intent = Intent(this, Milestone_info_walk::class.java)
                        startActivity(intent)
                        finish()
                    }

                }

            }

        BTN_back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
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
