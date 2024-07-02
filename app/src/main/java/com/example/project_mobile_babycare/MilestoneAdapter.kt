package com.example.project_mobile_babycare

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MilestoneAdapter(val context: Activity, val list: ArrayList<Milestone>) :
    ArrayAdapter<Milestone>(context, R.layout.item_milestone) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val context = context.layoutInflater
        val rowView = context.inflate(R.layout.item_milestone, parent, false)
        val ivIcon = rowView.findViewById<ImageView>(R.id.iv_icon)
        val name = rowView.findViewById<TextView>(R.id.tv_milestone_name)
        ivIcon.setImageResource(list[position].ivIcon)
        name.text = list[position].name
        return rowView
    }
}