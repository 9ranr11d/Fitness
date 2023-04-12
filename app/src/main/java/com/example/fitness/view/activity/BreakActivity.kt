package com.example.fitness.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.fitness.R
import com.example.fitness.data.TrainingRecord
import com.example.fitness.util.Utils
import com.example.fitness.databinding.ActivityBreakBinding
import com.example.fitness.databinding.DialogRecordBinding
import java.util.*
import kotlin.concurrent.timer

class BreakActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityBreakBinding
    private lateinit var recordBinding: DialogRecordBinding
    private val utils = Utils()

    private var timerTask: Timer? = null
    private var time = 0
    private lateinit var tempRecord: TrainingRecord
    private var isReserve = false
    private var isLock = true
    private val idMap = HashMap<String, Int>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityBreakBinding.inflate(layoutInflater)
        recordBinding = DialogRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLock(isLock)
        getObj()
        setProgressBar(time)
        setBtnListener()
        startTimeTask(time)
    }

    //객체 받아옴
    private fun getObj() {
        time = intent.getIntExtra("Break", -1)
        tempRecord = utils.getParcel(intent, "Temp_record")

        //값이 안 넘어왔을 경우
        if(time == -1) {
            Log.e(TAG, "There is no time value or setting value.")
            utils.makeToast(this, "다시 시도해 주세요")
            stopTimeTask(false)
        }

        binding.textBreakNum.text = "$time"
        binding.textBreakSet.text = "${++tempRecord.set}"
    }

    //Button Listener
    private fun setBtnListener() {
        binding.btnBreakStop.setOnClickListener(this)
        binding.btnBreakTemp.setOnClickListener(this)
        binding.btnBreakLock.setOnClickListener(this)
    }

    //Timer 시작
    private fun startTimeTask(outTime: Int) {
        var inTime = outTime + 1                        //오차 방지
        timerTask = timer(period = 1000) {  //1000ms == 1s
            inTime--

            runOnUiThread {
                binding.textBreakNum.text = "$inTime"
                binding.progressBreakRing.progress = inTime
            }

            //0초가 되면 실행
            if(inTime == 0) {
                runOnUiThread {
                    this.cancel()
                }
                stopTimeTask(true)
            }
        }
    }

    //Timer 정지 (isFinish : true -> 쉬는 시간 끝, false -> 중간에 정지)
    private fun stopTimeTask(isFinish: Boolean) {
        timerTask?.cancel()

        if(isFinish) {
            if(!isReserve) {
                val backOk = Intent()
                backOk.putExtra("Temp_record", tempRecord)

                setResult(RESULT_OK, backOk)
            }else {
                val backReserve = Intent()
                backReserve.putExtra("Reserve", tempRecord)

                setResult(Utils.RESULT_RESERVE, backReserve)
            }
        }
        else
            setResult(RESULT_CANCELED)

        finish()
    }

    //ProgressBar
    private fun setProgressBar(time: Int) {
        binding.progressBreakRing.min = 0
        binding.progressBreakRing.max = time
        binding.progressBreakRing.progress = time
    }

    @SuppressLint("SetTextI18n")
    private fun setRecordLay() {
        val partsList = utils.getPrefList(this, "Parts_list")
        recordBinding.textDRecordDate.text = "${tempRecord.date} ${tempRecord.time}"
        recordBinding.spinnerDRecordPart.adapter = utils.setSpinnerAdapter(this, partsList)
        recordBinding.spinnerDRecordPart.setSelection(partsList.indexOf(tempRecord.part))
        recordBinding.editDRecordName.setText(tempRecord.name)
        recordBinding.textDRecordSet.text = "${tempRecord.set}"

        recordBinding.gridDRecordRepAndWt.removeAllViews()

        val localRep = tempRecord.rep.split("_")
        val localWt = tempRecord.wt.split("_")
        for(i: Int in 1 .. tempRecord.set) {
            val repET = EditText(this)       //횟수 EditText
            val repID = View.generateViewId()       //ID
            repET.id = repID
            utils.setEdit(repET, "횟수")

            if(i - 1 < localRep.size)
                repET.setText(localRep[i - 1])

            val setTV = TextView(this)
            setTV.text = "$i"
            setTV.textSize = 15F

            val wtET = EditText(this)        //무게 EditText
            val wtID = View.generateViewId()        //ID
            wtET.id = wtID
            utils.setEdit(wtET, "무게")

            if(i - 1 < localWt.size)
                wtET.setText(localWt[i - 1])

            idMap["rep$i"] = repID
            idMap["wt$i"] = wtID

            recordBinding.gridDRecordRepAndWt.addView(repET)
            recordBinding.gridDRecordRepAndWt.addView(setTV)
            recordBinding.gridDRecordRepAndWt.addView(wtET)
        }
    }

    private fun setLock(isLock: Boolean) {
        if(isLock) {
            binding.btnBreakStop.isEnabled = false
            binding.btnBreakTemp.isEnabled = false
            binding.btnBreakLock.background = ContextCompat.getDrawable(this, R.drawable.img_unlock)
        }
        else {
            binding.btnBreakStop.isEnabled = true
            binding.btnBreakTemp.isEnabled = true
            binding.btnBreakLock.background = ContextCompat.getDrawable(this, R.drawable.img_lock)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_break_stop -> stopTimeTask(false)
            R.id.btn_break_temp -> {
                val recordLay = recordBinding.root
                setRecordLay()

                val reserveState =
                    if(!isReserve)
                        "예약"
                    else
                        "예약취소"

                val recordDialog = utils.initDialog(this, "임시저장")
                    ?.setView(recordLay)
                    ?.setPositiveButton("저장") { _, _ ->
                        tempRecord.part = recordBinding.spinnerDRecordPart.selectedItem.toString()
                        tempRecord.name = recordBinding.editDRecordName.text.toString()
                        tempRecord.rep = utils.getEditText(recordBinding.gridDRecordRepAndWt, idMap, tempRecord.set, "rep", "")
                        tempRecord.wt = utils.getEditText(recordBinding.gridDRecordRepAndWt, idMap, tempRecord.set, "wt", "")

                        if(recordLay.parent != null)
                            (recordLay.parent as ViewGroup).removeView(recordLay)
                    }
                    ?.setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()

                        if(recordLay.parent != null)
                            (recordLay.parent as ViewGroup).removeView(recordLay)
                    }
                    ?.setNeutralButton(reserveState) { _, _ ->
                        if(!isReserve) {
                            tempRecord.part = recordBinding.spinnerDRecordPart.selectedItem.toString()
                            tempRecord.name = utils.checkName(recordBinding.editDRecordName.text.toString())
                            tempRecord.rep = utils.getEditText(recordBinding.gridDRecordRepAndWt, idMap, tempRecord.set, "rep", "0")
                            tempRecord.wt = utils.getEditText(recordBinding.gridDRecordRepAndWt, idMap, tempRecord.set, "wt", "0")

                            binding.textBreakReserve.text = "예약 완료 : ${tempRecord.part}/${tempRecord.name}/${tempRecord.set}세트"
                            isReserve = true
                            Log.d(TAG, "Reserve")
                        }else {
                            binding.textBreakReserve.text = ""
                            isReserve = false
                            Log.d(TAG, "Reserve cancel")
                        }

                        if(recordLay.parent != null)
                            (recordLay.parent as ViewGroup).removeView(recordLay)
                    }
                    ?.create()

                recordDialog?.show()
            }
            R.id.btn_break_lock -> {
                val lockDialogMsg =
                    if(isLock)
                        "화면의 잠금을 해제하시겠습니까?"
                    else
                        "화면의 터치를 잠금 하시겠습니까?"

                val lockDialog = utils.initDialog(this, "화면 잠금")
                    ?.setMessage(lockDialogMsg)
                    ?.setPositiveButton("확인") { dialog, _ ->
                        dialog.dismiss()
                        isLock = !isLock

                        setLock(isLock)
                    }
                    ?.setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                        utils.makeToast(this, "취소되었습니다.")
                    }
                    ?.create()

                lockDialog?.show()
            }
        }
    }
}