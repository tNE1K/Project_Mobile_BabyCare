package com.example.project_mobile_babycare

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView

class InjectionAdapter(val context: Activity, val list: ArrayList<Injection>) :
    ArrayAdapter<Injection>(context, R.layout.item_bbinjec) {
    override fun getCount(): Int {
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        val context = context.layoutInflater
        val rowView = context.inflate(R.layout.item_bbinjec, parent, false)
        val name = rowView.findViewById<TextView>(R.id.txtName)
        val injected = rowView.findViewById<RadioButton>(R.id.rbt_datiem)
        val notInjected = rowView.findViewById<RadioButton>(R.id.rbt_chuatiem)
        name.text = list[position].name
        if(list[position].isInjected == true){
            injected.isChecked = true
            notInjected.isChecked = false
        }
        else{
            notInjected.isChecked = true
            injected.isChecked = false
        }
        return rowView
    }
    fun saveStatesToFirestore(){
        for(item in list){
            item.isInjected
        }
    }
}