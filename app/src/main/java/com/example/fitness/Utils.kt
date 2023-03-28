package com.example.fitness

import android.content.Context
import android.widget.ArrayAdapter

class Utils {
    //Spinner Adapter
    fun setSpinnerAdapter(context: Context, list: ArrayList<String>): ArrayAdapter<String> {
        return ArrayAdapter(context, android.R.layout.simple_list_item_1, list)
    }
}