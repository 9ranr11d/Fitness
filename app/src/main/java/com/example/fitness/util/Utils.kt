package com.example.fitness.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fitness.data.TrainingRecord
import com.example.fitness.view.activity.MainActivity
import org.json.JSONArray
import org.json.JSONException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow

class Utils {
    companion object {
        const val RESULT_INSERT = 1
        const val RESULT_UPDATE = 2
        const val RESULT_DELETE = 3
        const val RESULT_RESERVE = 4
    }

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

    //EditText 설정 ver.1
    fun setEdit(edit: EditText, hint: String) {
        edit.hint = hint
        edit.textSize = 15F
        edit.inputType = InputType.TYPE_CLASS_NUMBER
    }

    //동적 EditText 값 추출 ver.1
    fun getEditText(targetGrid: GridLayout, idMap: HashMap<String, Int>, size: Int, keyword: String, nullStr: String): String {
        val result = StringBuilder()

        for (i: Int in 1..size) {
            val value = "${targetGrid.findViewById<EditText>(idMap["$keyword$i"]!!).text}"

            //적힌 값이 없으면 0을 추가
            if(value.isNotEmpty())
                result.append(value)
            else
                result.append(nullStr)

            if(i < size)
                result.append("_")
        }

        return result.toString()
    }

    //숫자의 남은 자리를 0으로 채움
    fun intFullFormat(integer: Int, size: Int): String {
        val result = StringBuilder()

        if(integer != 0) {
            for(i: Int in size - 1 downTo 0) {
                if (integer < 10.0.pow(i.toDouble()))
                    result.append("0")
                else
                    break
            }

            result.append(integer.toString())
        }else {
            for(i: Int in 1 .. size) {
                result.append("0")
            }
        }

        return result.toString()
    }

    //Toast
    fun makeToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    //SharedPreference 리스트 추출
    fun getPrefList(context: Context, name: String): ArrayList<String> {
        val result = ArrayList<String>()

        val sharedPreferences = context.getSharedPreferences(MainActivity.utilFileName, AppCompatActivity.MODE_PRIVATE)
        val tempList = sharedPreferences.getString(name, null)

        try{
            val jsonAry = JSONArray(tempList)

            for(i: Int in 0 until jsonAry.length())
                result.add(jsonAry.optString(i))

        }catch(jsonE: JSONException) {
            jsonE.stackTrace
        }

        return result
    }

    //SharedPreference 리스트 저장
    fun setPrefList(context: Context, name: String, ary: List<String>) {
        val sharedPreferences = context.getSharedPreferences(
            MainActivity.utilFileName,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(name, initList(ary))
        editor.apply()
    }

    //AlertDialog
    fun initDialog(context: Context, title: String?): AlertDialog.Builder? {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setCancelable(false)
    }

    //List to jsonAry
    fun initList(list: List<String>): String {
        val jsonAry = JSONArray()

        for(one in list)
            jsonAry.put(one)

        return jsonAry.toString()
    }

    //이름이 비어있는지 확인
    fun checkName(name: String): String {
        if(name.isEmpty())
            return "Routine ${MainActivity.routine++}"

        return name
    }

    @Suppress("DEPRECATION")
    fun getParcel(intent: Intent, keyword: String): TrainingRecord =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(keyword, TrainingRecord::class.java)!!
        else
            intent.getParcelableExtra(keyword)!!

    fun getDateTime(pattern: String): String {
        val nowDate = LocalDateTime.now()
        val dateFormat = DateTimeFormatter.ofPattern(pattern)

        return nowDate.format(dateFormat).toString()
    }
}