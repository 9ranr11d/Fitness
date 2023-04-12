package com.example.fitness.view.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import com.example.fitness.*
import com.example.fitness.view.model.RecordListApplication
import com.example.fitness.view.model.RecordListViewModel
import com.example.fitness.view.model.RecordListViewModelFactory
import com.example.fitness.data.TrainingRecord
import com.example.fitness.databinding.ActivityOneRecordBinding
import com.example.fitness.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OneRecordActivity : AppCompatActivity(), View.OnClickListener {
//    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityOneRecordBinding
    private val utils = Utils()

    private lateinit var target: TrainingRecord
    private var isEdit = true
    private var partsList = ArrayList<String>()
    private val idMap = HashMap<String, Int>()

    private val recordListViewModel: RecordListViewModel by viewModels {
        RecordListViewModelFactory(
            (application as RecordListApplication).dataBase.TrainingRecordDAO()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityOneRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTarget()
        initPicker()
        initSpinner(target.part)
        initEdit()
        setBtnListener()
    }

    //Btn Listener
    private fun setBtnListener() {
        binding.btnORecordCheck.setOnClickListener(this)
        binding.btnORecordComplete.setOnClickListener(this)
        binding.btnORecordDelete.setOnClickListener(this)
        binding.btnORecordCancel.setOnClickListener(this)
    }

    //TrainingRecord 객체 받아옴
    private fun getTarget() {
        target = utils.getParcel(intent, "Target")
        isEdit = intent.getBooleanExtra("Is_edit", false)

        if(isEdit)
            binding.btnORecordComplete.text = resources.getString(R.string.str_update)
        else
            binding.btnORecordComplete.text = resources.getString(R.string.str_record)
    }

    //NumberPicker 설정
    private fun initPicker() {
        initDate(target.date)
        initTime(target.time)
    }

    //Spinner 설정
    private fun initSpinner(outPart: String) {
        partsList = utils.getPrefList(this, "Parts_list")

        binding.spinnerORecordPart.adapter = utils.setSpinnerAdapter(this, partsList)

        val position = partsList.indexOf(outPart)
        if(position != -1)
            binding.spinnerORecordPart.setSelection(position)
    }

    //Date numberPicker 초기값
    private fun initDate(outDate: String) {
        val inDate = outDate.split("-")

        binding.nPickerORecordYear.minValue = 2000
        binding.nPickerORecordYear.maxValue = 2100

        binding.nPickerORecordMonth.minValue = 1
        binding.nPickerORecordMonth.maxValue = 12

        binding.nPickerORecordDay.minValue = 1
        binding.nPickerORecordDay.maxValue = 31

        binding.nPickerORecordYear.value = inDate[0].toInt()
        binding.nPickerORecordMonth.value = inDate[1].toInt()
        binding.nPickerORecordDay.value = inDate[2].toInt()
    }

    //Time NumberPicker 초기값
    private fun initTime(outTime: String) {
        val inTime = outTime.split(":")

        binding.nPickerORecordHour.minValue = 1
        binding.nPickerORecordHour.maxValue = 24

        binding.nPickerORecordMinute.minValue = 0
        binding.nPickerORecordMinute.maxValue = 59

        binding.nPickerORecordHour.value = inTime[0].toInt()
        binding.nPickerORecordMinute.value = inTime[1].toInt()
    }

    //EditText 초기값 설정
    private fun initEdit() {
        binding.editORecordName.setText(target.name)
        binding.editORecordSet.setText(target.set.toString())

        initRepAndWt(target.set, target.rep, target.wt)
    }

    //횟수 및 무게 동적 뷰 생성
    @SuppressLint("SetTextI18n")
    private fun initRepAndWt(set: Int, outRep: String, outWt: String) {
        val inRep = outRep.split("_")
        val inWt = outWt.split("_")

        binding.gridORecordRepAndWt.removeAllViews()

        for(i: Int in 0 until set) {
            val repET = EditText(this)       //횟수 EditText
            val repID = View.generateViewId()       //ID
            repET.id = repID
            utils.setEdit(repET,"횟수")

            if(i < inRep.size)
                repET.setText(inRep[i])

            val setTV = TextView(this)
            setTV.text = "${i + 1}"
            setTV.textSize = 15F

            val wtET = EditText(this)        //무게 EditText
            val wtID = View.generateViewId()        //ID
            wtET.id = wtID
            utils.setEdit(wtET, "무게")

            if(i < inWt.size)
                wtET.setText(inWt[i])

            idMap["rep${i + 1}"] = repID
            idMap["wt${i + 1}"] = wtID

            binding.gridORecordRepAndWt.addView(repET)
            binding.gridORecordRepAndWt.addView(setTV)
            binding.gridORecordRepAndWt.addView(wtET)
        }
    }

    private fun insertRecord(record: TrainingRecord) {
        CoroutineScope(Dispatchers.IO).launch {
            recordListViewModel.insertRecord(record)
        }
    }

    //수정 버튼
    private fun updateRecord(record: TrainingRecord) {
        CoroutineScope(Dispatchers.IO).launch {
            recordListViewModel.updateRecord(record)
        }
    }


    //삭제 버튼
    private fun deleteRecord(record: TrainingRecord) {
        CoroutineScope(Dispatchers.IO).launch {
            recordListViewModel.deleteRecord(record)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_o_record_check -> initRepAndWt(binding.editORecordSet.text.toString().toInt(), target.rep, target.wt)
            R.id.btn_o_record_complete -> {
                val updateMonth = utils.intFullFormat(binding.nPickerORecordMonth.value, 2)
                val updateDay = utils.intFullFormat(binding.nPickerORecordDay.value, 2)
                val updateHour = utils.intFullFormat(binding.nPickerORecordHour.value, 2)
                val updateMinute = utils.intFullFormat(binding.nPickerORecordMinute.value, 2)

                val updateDate = "${binding.nPickerORecordYear.value}-$updateMonth-$updateDay"
                val updateTime = "$updateHour:$updateMinute"
                val updateSet = binding.editORecordSet.text.toString().toInt()

                target.date = updateDate
                target.time = updateTime
                target.part = "${binding.spinnerORecordPart.selectedItem}"
                target.name = utils.checkName("${binding.editORecordName.text}")
                target.set = updateSet
                target.rep = utils.getEditText(binding.gridORecordRepAndWt, idMap, updateSet, "rep", "0")
                target.wt = utils.getEditText(binding.gridORecordRepAndWt, idMap, updateSet, "wt", "0")

                if(isEdit) {
                    updateRecord(target)
                    setResult(Utils.RESULT_UPDATE)
                }else {
                    insertRecord(target)
                    setResult(Utils.RESULT_INSERT)
                }
                finish()
            }
            R.id.btn_o_record_delete -> {
                deleteRecord(target)
                setResult(Utils.RESULT_DELETE)
                finish()
            }
            R.id.btn_o_record_cancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}