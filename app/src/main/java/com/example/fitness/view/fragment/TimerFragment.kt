package com.example.fitness.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.fitness.*
import com.example.fitness.view.model.RecordListApplication
import com.example.fitness.view.model.RecordListViewModel
import com.example.fitness.view.model.RecordListViewModelFactory
import com.example.fitness.data.TrainingRecord
import com.example.fitness.databinding.DialogRecordBinding
import com.example.fitness.databinding.FragmentTimerBinding
import com.example.fitness.util.Utils
import com.example.fitness.view.activity.BreakActivity
import com.example.fitness.view.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TimerFragment : Fragment(), View.OnClickListener {
    private val TAG = javaClass.simpleName
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private var _recordBinding: DialogRecordBinding? = null
    private val recordBinding get() = _recordBinding!!
    private val utils = Utils()

    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var set = 0
    private var min = 0
    private var sec = 0
    private lateinit var tempRecord: TrainingRecord
    private var partsList = ArrayList<String>()
    private val idMap = HashMap<String, Int>()

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
        _recordBinding = DialogRecordBinding.inflate(inflater, container, false)     //dialog_record
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirst()
        getPref()
        initSetting()
        initNumPicker()
        setBtnListener()
        getLauncherResult()
    }

    //SharedPreferences 데이터 가져오기
    private fun getPref() {
        val sharedPreferences = requireActivity().getSharedPreferences(MainActivity.utilFileName, Activity.MODE_PRIVATE)
        val tempBreak = sharedPreferences.getInt("Break", 60)

        min = tempBreak / 60
        sec = tempBreak % 60
    }

    //SharedPreferences 데이터 저장
    private fun setPref() {
        min = binding.nPickerTimerMin.value
        sec = binding.nPickerTimerSec.value

        val sharedPreferences = requireActivity().getSharedPreferences(MainActivity.utilFileName, Activity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("Break", (min * 60) + sec)
        editor.apply()
    }

    private fun initSetting() {
        binding.textTimerSet.text = "$set"

        val tempDateTime = utils.getDateTime("yyyy-MM-dd HH:mm").split(" ")
        tempRecord = TrainingRecord(0, tempDateTime[0], tempDateTime[1], "", "", set, "", "")
    }

    //맨 처음 딱 한번 실행
    private fun setFirst() {
        val sharedPreferences = requireActivity().getSharedPreferences(MainActivity.utilFileName, Activity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val isFirst = sharedPreferences.getBoolean("Is_first", true)

        //맨 처음 시작할 때만 실행
        if(isFirst) {
            Log.d(TAG, "First visit.")

            editor.putBoolean("Is_first", false)
            editor.putString("Parts_list", utils.initList(resources.getStringArray(R.array.init_parts_list).toList()))
            editor.putString("Colors_list", utils.initList(resources.getStringArray(R.array.init_colors_list).toList()))
            editor.apply()
        }else
            Log.d(TAG, "Not the first visit.")
    }

    //Parts list 가져오기
    private fun getPartsList() {
        val tempPartsList = utils.getPrefList(requireContext(), "Parts_list")

        if(!Arrays.equals(partsList.toArray(), tempPartsList.toArray()))
            partsList = tempPartsList
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

    //Btn Listener
    private fun setBtnListener() {
        binding.btnTimerSetPlus.setOnClickListener(this)
        binding.btnTimerSetMinus.setOnClickListener(this)
        binding.btnTimerRecord.setOnClickListener(this)
        binding.btnTimerStart.setOnClickListener(this)
    }

    //Launcher Result
    private fun getLauncherResult() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_OK -> {
                    val tempDateTime = utils.getDateTime("yyyy-MM-dd HH:mm").split(" ")
                    tempRecord.date = tempDateTime[0]
                    tempRecord.time = tempDateTime[1]
                    tempRecord = utils.getParcel(it.data!!, "Temp_record")
                }
                Activity.RESULT_CANCELED -> utils.makeToast(requireContext(), "정지되었습니다.")
                Utils.RESULT_RESERVE -> {
                    insertRecord(utils.getParcel(it.data!!, "Reserve"))
                    utils.makeToast(requireContext(), "기록에 성공했습니다.")
                    binding.textTimerSet.text = "0"
                }
            }
        }
    }

    //기록 팝업 setting
    @SuppressLint("SetTextI18n")
    private fun setRecordLay() {
        recordBinding.textDRecordDate.text = "${tempRecord.date} ${tempRecord.time}"
        recordBinding.textDRecordSet.text = "$set"
        recordBinding.spinnerDRecordPart.adapter = utils.setSpinnerAdapter(requireContext(), partsList)

        recordBinding.gridDRecordRepAndWt.removeAllViews()

        val localRep = tempRecord.rep.split("_")
        val localWt = tempRecord.wt.split("_")
        for(i: Int in 1 .. set) {
            val repET = EditText(context)       //횟수 EditText
            val repID = View.generateViewId()   //ID
            repET.id = repID
            utils.setEdit(repET,"횟수")
            repET.imeOptions = EditorInfo.IME_ACTION_NEXT

            if(i - 1 < localRep.size)
                repET.setText(localRep[i - 1])

            val setTV = TextView(context)
            setTV.text = "${i}회"
            setTV.textSize = 15F

            val wtET = EditText(context)        //무게 EditText
            val wtID = View.generateViewId()    //ID
            wtET.id = wtID
            utils.setEdit(wtET, "무게")

            if(i < set)
                wtET.imeOptions = EditorInfo.IME_ACTION_NEXT

            if(i - 1 < localWt.size)
                wtET.setText(localWt[i - 1])

            idMap["rep$i"] = repID
            idMap["wt$i"] = wtID

            recordBinding.gridDRecordRepAndWt.addView(repET)
            recordBinding.gridDRecordRepAndWt.addView(setTV)
            recordBinding.gridDRecordRepAndWt.addView(wtET)
        }
    }

    //DB에 데이터 추가
    private fun insertRecord(record: TrainingRecord) {
        CoroutineScope(Dispatchers.IO).launch {
            recordListViewModel.insertRecord(record)
            set = 0
            tempRecord.set = set
            tempRecord.rep = ""
            tempRecord.wt = ""
            idMap.clear()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_timer_set_plus -> binding.textTimerSet.text = "${++set}"       //세트 수 증가
            R.id.btn_timer_set_minus -> {                                                   //세트 수 감소
                if(set > 0)
                    binding.textTimerSet.text = "${--set}"
            }
            R.id.btn_timer_record -> {                                                      //기록
                if(set > 0) {
                    val recordLay = recordBinding.root
                    setRecordLay()

                    val recordDialog = utils.initDialog(requireContext(), "기록")
                        ?.setView(recordLay)
                        ?.setPositiveButton("추가") { dialog, _ ->
                            dialog.dismiss()
                            val tempDateTime = utils.getDateTime("yyyy-MM-dd HH:mm").split(" ")
                            tempRecord.date = tempDateTime[0]
                            tempRecord.time = tempDateTime[1]
                            tempRecord.part = "${recordBinding.spinnerDRecordPart.selectedItem}"
                            tempRecord.name = utils.checkName("${recordBinding.editDRecordName.text}")
                            tempRecord.set = set
                            tempRecord.rep = utils.getEditText(recordBinding.gridDRecordRepAndWt, idMap, tempRecord.set, "rep", "0")
                            tempRecord.wt = utils.getEditText(recordBinding.gridDRecordRepAndWt, idMap, tempRecord.set, "wt", "0")

                            insertRecord(tempRecord)
                            utils.makeToast(requireContext(), "기록에 성공했습니다.")
                            binding.textTimerSet.text = "0"

                            if(recordLay.parent != null)
                                (recordLay.parent as ViewGroup).removeView(recordLay)
                        }
                        ?.setNegativeButton("취소") { dialog, _ ->
                            dialog.dismiss()
                            utils.makeToast(requireContext(), "취소되었습니다.")

                            if(recordLay.parent != null)
                                (recordLay.parent as ViewGroup).removeView(recordLay)
                        }
                        ?.create()

                    recordDialog?.show()
                }else
                    utils.makeToast(requireContext(), "세트 수가 없습니다.")
            }
            R.id.btn_timer_start -> {                                                       //타이머 시작
                val goBreak = Intent(requireContext(), BreakActivity::class.java)
                val time = (binding.nPickerTimerMin.value * 60) + binding.nPickerTimerSec.value
                goBreak.putExtra("Break", time)
                goBreak.putExtra("Temp_record", tempRecord)

                launcher.launch(goBreak)
            }
        }
    }

    //Parts list 변경
    override fun onStart() {
        super.onStart()
        getPartsList()
    }

    override fun onStop() {
        super.onStop()
        setPref()
    }

    //Fragment ViewBinding 삭제
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _recordBinding = null
    }
}