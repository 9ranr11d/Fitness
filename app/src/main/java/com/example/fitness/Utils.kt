package com.example.fitness

import android.content.Context
import android.widget.ArrayAdapter

class Utils {
    //Spinner Adapter
    fun setSpinnerAdapter(context: Context, list: ArrayList<String>): ArrayAdapter<String> {
        return ArrayAdapter(context, android.R.layout.simple_list_item_1, list)
    }

    //first X second 형식으로 묶음
    fun crossStr(set: Int, first: String, second: String): String {
        val resultBuilder = StringBuilder()

        val firstAry = first.split("_")
        val secondAry = second.split("_")

        for(i: Int in 0 until set) {
            resultBuilder
                .append(firstAry[i])
                .append("X")
                .append(secondAry[i])

            if(i < set - 1)
                resultBuilder.append(",")
        }

        return resultBuilder.toString()
    }
}