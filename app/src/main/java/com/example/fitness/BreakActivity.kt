package com.example.fitness

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.fitness.databinding.ActivityBreakBinding
import java.util.*
import kotlin.concurrent.timer

class BreakActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "BreakActivity"
    private lateinit var binding: ActivityBreakBinding

    private var timerTask: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityBreakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var time = intent.getIntExtra("Break", -1)
        val set = intent.getIntExtra("Set", -1)

        //값이 안넘어왔을 경우
        if(time == -1 || set == -1) {
            Log.e(TAG, "There is no time value or setting value.")
            Toast.makeText(this, "다시 시도해 주세요", Toast.LENGTH_SHORT).show()
            stopTimeTask(false)
        }

        binding.textBreakNum.text = "$time"
        binding.textBreakSet.text = "$set"

        setProgressBar(time)
        setBtnListener()
        startTimeTask(time)
    }

    //Button Listener
    private fun setBtnListener() {
        binding.btnBreakStop.setOnClickListener(this)
    }

    //Timer 시작
    private fun startTimeTask(outTime: Int) {
        var inTime = outTime + 1    //오차 방지
        timerTask = timer(period = 1000) {  //1000ms == 1s
            inTime--

            runOnUiThread {
                binding.textBreakNum.text = "$inTime"
                binding.progressBarBreakRing.progress = inTime
            }

            //0초가 되면 실행
            if(inTime == 0) {
                runOnUiThread {
                    this?.cancel()
                }
                stopTimeTask(true)
            }
        }
    }

    //Timer 정지 (isFinish : true -> 쉬는 시간 끝, false -> 중간에 정지)
    private fun stopTimeTask(isFinish: Boolean) {
        timerTask?.cancel()
        val back = Intent()
        back.putExtra("isFinish", isFinish)

        setResult(RESULT_OK, back)
        finish()
    }

    //ProgressBar
    private fun setProgressBar(time: Int) {
        binding.progressBarBreakRing.min = 0
        binding.progressBarBreakRing.max = time
        binding.progressBarBreakRing.progress = time
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_break_stop -> stopTimeTask(false)
        }
    }
}