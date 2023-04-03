package com.example.fitness

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
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
    private val TAG = javaClass.simpleName
    private var _binding: FragmentTimerBinding? = null              //FragmentTimer ViewBinding
    private val binding get() = _binding!!
    private var _dialogBinding: DialogRecordBinding? = null         //DialogRecord ViewBinding
    private val dialogBinding get() = _dialogBinding!!
    private val utils = Utils()

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var set = 0                                 //세트
    private var min = 0                                 //분
    private var sec = 0                                 //초
    private var partsList = ArrayList<String>()         //운동 부위 List
    private var editIdMap = HashMap<String, Int>()      //동적 뷰 ID Map

    private val recordListViewModel: RecordListViewModel by activityViewModels {
        RecordListViewModelFactory(
            (activity?.application as RecordListApplication).dataBase.TrainingRecordDAO()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //ViewBinding
        _binding = FragmentTimerBinding.inflate(inflater, container, false)          //fragment_timer
        _dialogBinding = DialogRecordBinding.inflate(inflater, container, false)     //dialog_record
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirst()

        getSharedPreferences()

        initSetting()
        setBtnListener()

        getLauncherResult()
    }

    private fun initSetting() {
        binding.textTimerSet.text = "$set"
        initNumPicker()
    }

    //맨 처음 딱 한번 실행
    private fun setFirst() {
        val sharedPreferences = this.requireActivity().getSharedPreferences(MainActivity.utilFileName, Activity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val isFirst = sharedPreferences.getBoolean("Is_first", true)

        //맨 처음 시작할 때만 실행
        if(isFirst) {
            Log.d(TAG, "First visit")

            val firstPartsList = ArrayList<String>(listOf("가슴", "등", "하체", "어깨", "삼두", "이두", "복근"))
            val jsonAry = JSONArray()

            for(part in firstPartsList)
                jsonAry.put(part)

            editor.putBoolean("Is_first", false)
            editor.putString("Parts_list", jsonAry.toString())
            editor.apply()
        }else
            Log.d(TAG, "Not the first visit")
    }

    //SharedPreferences 저장된 변수 가져오기
    private fun getSharedPreferences() {
        val sharedPreferences = this.requireActivity().getSharedPreferences(MainActivity.utilFileName, Activity.MODE_PRIVATE)
        val tempPartsList = sharedPreferences.getString("Parts_list", null)

        if(partsList.isEmpty()) {
            try {
                val jsonAry = JSONArray(tempPartsList)

                for(i: Int in 0 until jsonAry.length())
                    partsList.add(jsonAry.optString(i))

            }catch(jsonE: JSONException) {
                jsonE.stackTrace
            }
        }
    }

    //NumberPicker setting
    private fun initNumPicker() {
        //쉬는 시간의 분 numberPicker 최대, 최소 값 지정
        binding.nPickerTimerMin.minValue = 0
        binding.nPickerTimerMin.maxValue = 10

        //쉬는 시간의 초 numberPicker 최대, 최소 값 지정
        binding.nPickerTimerSec.minValue = 0
        binding.nPickerTimerSec.maxValue = 59

        binding.nPickerTimerMin.value = min
        binding.nPickerTimerSec.value = sec
    }

    //Button Listener
    private fun setBtnListener() {
        binding.btnTimerSetPlus.setOnClickListener(this)
        binding.btnTimerSetMinus.setOnClickListener(this)
        binding.btnTimerRecord.setOnClickListener(this)
        binding.btnTimerStart.setOnClickListener(this)
    }

    //Launcher Result 값 받아옴
    private fun getLauncherResult() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_OK -> binding.textTimerSet.text = "${++set}"
                Activity.RESULT_CANCELED -> utils.makeToast(requireContext(), "정지되었습니다.")
            }
        }
    }

    //기록 팝업 Setting
    private fun serRecordView() {
        val nowDate = LocalDateTime.now()
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        dialogBinding.textRecordDate.text = nowDate.format(dateFormat)
        dialogBinding.textRecordSet.text = set.toString()
        dialogBinding.spinnerRecordPart.adapter = utils.setSpinnerAdapter(requireContext(), partsList)

        dialogBinding.gridRecordRepAndWt.removeAllViews()

        for(i: Int in 1 .. set) {
            val repET = EditText(context)       //횟수 EditText
            val repID = View.generateViewId()   //ID
            repET.id = repID
            utils.setEdit(repET,"횟수")

            val setTV = TextView(context)
            setTV.text = "$i"
            setTV.textSize = 15F

            val wtET = EditText(context)        //무게 EditText
            val wtID = View.generateViewId()    //ID
            wtET.id = wtID
            utils.setEdit(wtET, "무게")

            editIdMap["rep$i"] = repID
            editIdMap["wt$i"] = wtID

            dialogBinding.gridRecordRepAndWt.addView(repET)
            dialogBinding.gridRecordRepAndWt.addView(setTV)
            dialogBinding.gridRecordRepAndWt.addView(wtET)
        }
    }

    //DB에 데이터 추가
    private fun insertRecord(record: TrainingRecord) {
        CoroutineScope(Dispatchers.IO).launch {
            recordListViewModel.insertRecord(record)
        }
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
                                utils.getEditText(dialogBinding.gridRecordRepAndWt, editIdMap, set, "rep"),
                                utils.getEditText(dialogBinding.gridRecordRepAndWt, editIdMap, set, "wt")
                            )
                            insertRecord(recordToAdd)
                            editIdMap.clear()           //기록 완료 후 잔여 데이터 삭제
                            set = 0
                            utils.makeToast(requireContext(), "기록에 성공하였습니다.")

                            //recordView 중복 문제 해결
                            if (recordView.parent != null)
                                (recordView.parent as ViewGroup).removeView(recordView)
                        }
                        .setNegativeButton("취소") { dialog, _ ->
                            if (recordView.parent != null)
                                (recordView.parent as ViewGroup).removeView(recordView)

                            dialog.dismiss()
                            utils.makeToast(requireContext(), "취소되었습니다.")
                        }

                    recordDialog.show()
                }else
                    utils.makeToast(requireContext(), "세트 수가 없습니다.")
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

    //Fragment ViewBinding 삭제
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _dialogBinding = null
    }
}