package com.example.project_mobile_babycare

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HeightWeightAdapter(val context: Activity, val list: ArrayList<HeightWeight>) :
    ArrayAdapter<HeightWeight>(context, R.layout.item_heightweight) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val context = context.layoutInflater
        val rowView = context.inflate(R.layout.item_heightweight, parent, false)
        val addDay = rowView.findViewById<TextView>(R.id.tv_addDay)
        val heightWeight = rowView.findViewById<TextView>(R.id.tv_heightWeight)
        addDay.text = list[position].start
        heightWeight.text = list[position].end
        return rowView
    }
}