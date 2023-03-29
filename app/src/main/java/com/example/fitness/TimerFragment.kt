package com.example.fitness

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Room
import com.example.fitness.databinding.DialogRecordBinding
import com.example.fitness.databinding.FragmentTimerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class TimerFragment : Fragment(), View.OnClickListener {
    private val tag = javaClass.simpleName
    private lateinit var binding: FragmentTimerBinding              //FragmentTimer ViewBinding
    private lateinit var dialogBinding: DialogRecordBinding         //DialogRecord ViewBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private val utils = Utils()
    private val utilFileName = "utilVar"

    private var set = 0                                 //세트
    private var partsList = ArrayList<String>()         //운동 부위 List
    private var editIdMap = HashMap<String, Int>()      //동적 뷰 ID Map

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //ViewBinding
        binding = FragmentTimerBinding.inflate(layoutInflater)          //fragment_timer
        dialogBinding = DialogRecordBinding.inflate(layoutInflater)     //dialog_record

        setFirst()

        getSharedPreferences()

        initNumPicker()
        setBtnListener()
        getLauncherResult()

        return binding.root
    }

    //SharedPreferences 저장된 변수 가져오기
    private fun getSharedPreferences() {
        val sharedPreferences = this.requireActivity().getSharedPreferences(utilFileName, Activity.MODE_PRIVATE)
        val tempPartsList = sharedPreferences.getString("Parts_list", null)
        if(tempPartsList != null) {
            try {
                val jsonAry = JSONArray(tempPartsList)

                for(i: Int in 0 until jsonAry.length())
                    partsList.add(jsonAry.optString(i))

            }catch(jsonE: JSONException) {
                jsonE.stackTrace
            }
        }
    }

    //맨 처음 딱 한번 실행
    private fun setFirst() {
        val sharedPreferences = this.requireActivity().getSharedPreferences(utilFileName, Activity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val isFirst = sharedPreferences.getBoolean("Is_first", true)

        //맨 처음 시작할 때만 실행
        if(isFirst) {
            Log.d(tag, "First visit")

            val firstPartsList = ArrayList<String>(listOf("가슴", "등", "하체", "어깨", "삼두", "이두", "복근"))
            val jsonAry = JSONArray()

            for(part in firstPartsList)
                jsonAry.put(part)

            editor.putBoolean("Is_first", false)
            editor.putString("Parts_list", jsonAry.toString())
            editor.apply()
        }else
            Log.d(tag, "Not the first visit")
    }

    //Launcher Result 값 받아옴
    private fun getLauncherResult() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_OK -> {
                    val receive = it.data
                    if(receive?.getBooleanExtra("isFinish", false)!!)
                        binding.textTimerSet.text = "${++set}"
                }
            }
        }
    }

    //Button Listener
    private fun setBtnListener() {
        binding.btnTimerSetPlus.setOnClickListener(this)
        binding.btnTimerSetMinus.setOnClickListener(this)
        binding.btnTimerRecord.setOnClickListener(this)
        binding.btnTimerStart.setOnClickListener(this)
    }

    //NumberPicker setting
    private fun initNumPicker() {
        //쉬는 시간의 분 numberPicker 최대, 최소 값 지정
        binding.nPickerTimerMin.minValue = 0
        binding.nPickerTimerMin.maxValue = 10

        //쉬는 시간의 초 numberPicker 최대, 최소 값 지정
        binding.nPickerTimerSec.minValue = 0
        binding.nPickerTimerSec.maxValue = 59
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_timer_set_plus -> binding.textTimerSet.text = (++set).toString()       //세트 수 증가
            R.id.btn_timer_set_minus -> {                                                   //세트 수 감소
                if(set > 0)
                    binding.textTimerSet.text = (--set).toString()
            }
            R.id.btn_timer_record -> {                                                      //기록
                if(set > 0) {
                    val recordView = dialogBinding.root
                    serRecordView()

                    val recordDialog = AlertDialog.Builder(requireContext())
                        .setTitle("기록")
                        .setView(recordView)
                        .setCancelable(false)
                        .setPositiveButton("추가") { _, _ ->
                            val recordToAdd = TrainingRecord(
                                0,
                                dialogBinding.textRecordDate.text.toString(),
                                dialogBinding.spinnerRecordPart.selectedItem.toString(),
                                dialogBinding.editRecordName.text.toString(),
                                dialogBinding.textRecordSet.text.toString().toInt(),
                                getEditText("rep"),
                                getEditText("wt")
                            )
                            insertDB(recordToAdd)
                            editIdMap.clear()           //기록 완료 후 잔여 데이터 삭제

                            //recordView 중복 문제 해결
                            if (recordView.parent != null)
                                (recordView.parent as ViewGroup).removeView(recordView)
                        }
                        .setNegativeButton("취소") { dialog, _ ->
                            if (recordView.parent != null)
                                (recordView.parent as ViewGroup).removeView(recordView)

                            dialog.dismiss()
                            Toast.makeText(requireContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show()
                        }

                    recordDialog.show()
                }else
                    Toast.makeText(requireContext(), "세트 수가 없습니다.", Toast.LENGTH_SHORT).show()
            }
            R.id.btn_timer_start -> {                                                       //타이머 시작
                val goBreak = Intent(requireContext(), BreakActivity::class.java)
                val time = (binding.nPickerTimerMin.value * 60) + binding.nPickerTimerSec.value
                var tempSet = binding.textTimerSet.text.toString().toInt()
                goBreak.putExtra("Break", time)
                goBreak.putExtra("Set", ++tempSet)

                launcher.launch(goBreak)
            }
        }
    }

    //동적으로 생성한 EditText 값 추출
    private fun getEditText(keyword: String): String {
        val result = StringBuilder()

        for (i: Int in 1..set) {
            val value = "${dialogBinding.gridLayRecordRepAndWt.findViewById<EditText>(editIdMap["$keyword$i"]!!).text}"

            //적힌 값이 없으면 0을 추가
            if(value.isNotEmpty())
                result.append(value)
            else
                result.append("0")

            result.append("_")
        }

        return result.toString()
    }

    //기록 팝업 Setting
    private fun serRecordView() {
        val nowDate = LocalDateTime.now()
        val dateFormat = DateTimeFormatter.ofPattern("yy-MM-dd_HH:mm")
        dialogBinding.textRecordDate.text = nowDate.format(dateFormat)
        dialogBinding.textRecordSet.text = set.toString()
        dialogBinding.spinnerRecordPart.adapter = utils.setSpinnerAdapter(requireContext(), partsList)

        dialogBinding.gridLayRecordRepAndWt.removeAllViews()

        for(i: Int in 1 .. set) {
            val repET = EditText(context)       //횟수 EditText
            val repID = View.generateViewId()   //ID
            repET.id = repID
            setEdit(repET,"횟수")

            val setTV = TextView(context)
            setTV.text = "$i"
            setTV.textSize = 15F

            val wtET = EditText(context)        //무게 EditText
            val wtID = View.generateViewId()    //ID
            wtET.id = wtID
            setEdit(wtET, "무게")

            editIdMap["rep$i"] = repID
            editIdMap["wt$i"] = wtID

            dialogBinding.gridLayRecordRepAndWt.addView(repET)
            dialogBinding.gridLayRecordRepAndWt.addView(setTV)
            dialogBinding.gridLayRecordRepAndWt.addView(wtET)
        }
    }

    //횟수, 무게 EditText Setting
    private fun setEdit(edit: EditText, hint: String) {
        edit.hint = hint
        edit.textSize = 15F
        edit.inputType = InputType.TYPE_CLASS_NUMBER
    }

    //DB에 데이터 추가
    private fun insertDB(target: TrainingRecord) {
        val db = Room.databaseBuilder(requireContext(), DataBase::class.java, "TrainingRecord").build()
        val trainingRecordDAO = db.TrainingRecordDAO()

        CoroutineScope(Dispatchers.IO).launch {
            trainingRecordDAO.insertRecord(target)
        }
    }
}