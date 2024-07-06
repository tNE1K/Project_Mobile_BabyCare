package com.example.project_mobile_babycare

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class MemoryAdapter (val context: Activity, val list: ArrayList<Memory>) :
    ArrayAdapter<Memory>(context, R.layout.item_memory) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val context = context.layoutInflater
        val rowView = context.inflate(R.layout.item_memory, parent, false)
        val imageView = rowView.findViewById<ImageView>(R.id.item_memory_iv)
        val note = rowView.findViewById<TextView>(R.id.tv_MemoryName)
        val imageUrl = list[position].imageUrl
        Glide.with(context.context)
            .load(imageUrl)
            .into(imageView)
        note.text = list[position].note
        return rowView
    }
}