package com.example.fitness

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
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
import java.time.LocalDate

class TimerFragment : Fragment(), View.OnClickListener {
    private val TAG = "TimerFragment"
    private lateinit var binding: FragmentTimerBinding
    private lateinit var dialogBinding: DialogRecordBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var set = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //ViewBinding
        binding = FragmentTimerBinding.inflate(layoutInflater)          //fragment_timer
        dialogBinding = DialogRecordBinding.inflate(layoutInflater)     //dialog_record

        setNumPicker()
        setBtnListener()
        getLauncherResult()

        return binding.root
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
        binding.btnTimerBreakPlus.setOnClickListener(this)
        binding.btnTimerBreakMinus.setOnClickListener(this)
        binding.btnTimerRecord.setOnClickListener(this)
        binding.btnTimerStart.setOnClickListener(this)
    }

    //NumberPicker setting
    private fun setNumPicker() {
        //쉬는 시간의 분 numberPicker 최대, 최소 값 지정
        binding.nPickerTimerMin.minValue = 0
        binding.nPickerTimerMin.maxValue = 10

        //쉬는 시간의 초 numberPicker 최대, 최소 값 지정
        binding.nPickerTimerSec.minValue = 0
        binding.nPickerTimerSec.maxValue = 59
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_timer_break_plus -> binding.textTimerSet.text = (++set).toString()
            R.id.btn_timer_break_minus -> {
                if(set > 0)
                    binding.textTimerSet.text = (--set).toString()
            }
            R.id.btn_timer_record -> {
                val recordView = dialogBinding.root
                serRecordView()

                val recordDialog = AlertDialog.Builder(requireContext())
                    .setTitle("기록")
                    .setView(recordView)
                    .setCancelable(false)
                    .setPositiveButton("추가", DialogInterface.OnClickListener() {
                        _, _ ->
                            val recordToAdd = TrainingRecord(
                                0,
                                dialogBinding.textRecordDate.text.toString(),
                                dialogBinding.spinnerRecordPart.selectedItem.toString(),
                                dialogBinding.editRecordName.text.toString(),
                                dialogBinding.textRecordSet.text.toString().toInt(),
                                TODO("rep"),
                                TODO("wt")
                            )
                            insertDB(recordToAdd)

                            //recordView의 중복 문제 해결
                            if(recordView.parent != null)
                                (recordView.parent as ViewGroup).removeView(recordView)
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener() {
                        dialog, _ ->
                            if(recordView.parent != null)
                                (recordView.parent as ViewGroup).removeView(recordView)

                            dialog.dismiss()
                            Toast.makeText(requireContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show()
                    })

                recordDialog.show()
            }
            R.id.btn_timer_start -> {
                val goBreak = Intent(requireContext(), BreakActivity::class.java)
                val time = (binding.nPickerTimerMin.value * 60) + binding.nPickerTimerSec.value
                var tempSet = binding.textTimerSet.text.toString().toInt()
                goBreak.putExtra("Break", time)
                goBreak.putExtra("Set", ++tempSet)

                launcher.launch(goBreak)
            }
        }
    }

    //기록 팝업 Setting
    private fun serRecordView() {
        dialogBinding.textRecordDate.text = LocalDate.now().toString()
        dialogBinding.textRecordSet.text = set.toString()

        dialogBinding.gridLayRecordRepAndWt.removeAllViews()

        for(i: Int in 1 .. set) {
            val repET = EditText(context)
            setEdit(repET,"횟수")

            val setTV = TextView(context)
            setTV.text = "${i}회"
            setTV.textSize = 15F

            val wtET = EditText(context)
            setEdit(wtET, "무게")

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