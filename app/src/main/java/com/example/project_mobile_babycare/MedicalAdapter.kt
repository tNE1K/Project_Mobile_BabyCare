package com.example.project_mobile_babycare

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MedicalAdapter(val context: Activity, val list: ArrayList<Medical>) :
    ArrayAdapter<Medical>(context, R.layout.item_medical) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val context = context.layoutInflater
        val rowView = context.inflate(R.layout.item_medical, parent, false)
        val startDay = rowView.findViewById<TextView>(R.id.tv_medicalStartDay)
        val endDay = rowView.findViewById<TextView>(R.id.tv_medicalEndDay)
        startDay.text = list[position].start
        endDay.text = list[position].end
        return rowView
    }
}