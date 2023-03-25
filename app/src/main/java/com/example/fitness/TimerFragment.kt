package com.example.fitness

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fitness.databinding.FragmentTimerBinding

class TimerFragment : Fragment(), View.OnClickListener {
    private val TAG = "TimerFragment"
    private lateinit var binding: FragmentTimerBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var set = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //ViewBinding
        binding = FragmentTimerBinding.inflate(layoutInflater)

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

    //NumberPicker 셋팅
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
}