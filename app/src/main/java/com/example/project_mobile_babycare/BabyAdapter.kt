package com.example.project_mobile_babycare


import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BabyAdapter(val context: Activity, val list: ArrayList<Baby>) :
    ArrayAdapter<Baby>(context, R.layout.item_baby) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val context = context.layoutInflater
        val rowView = context.inflate(R.layout.baby_selected, parent, false)
        val name = rowView.findViewById<TextView>(R.id.tv_selectedBaby)
        name.text = "Xin chào bé " + list[position].name
        return rowView
     }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val context_ = context.layoutInflater
        val rowView = context_.inflate(R.layout.item_baby, parent, false)
        val name = rowView.findViewById<TextView>(R.id.tv_babyName)
        val btnAdd = rowView.findViewById<Button>(R.id.btnAddBaby)
        val imgIgnore = rowView.findViewById<ImageView>(R.id.IV_babyIgnore)
        name.text = list[position].name
        btnAdd.setOnClickListener {
            val intent = Intent(context, AddBaby::class.java)
            context.startActivity(intent)
        }
        if (position == list.size - 1) {
            btnAdd.visibility = View.VISIBLE
            imgIgnore.visibility = View.GONE
        }

        imgIgnore.setOnClickListener{

        }
        return rowView
    }
}