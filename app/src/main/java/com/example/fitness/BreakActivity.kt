package com.example.fitness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.fitness.databinding.ActivityBreakBinding
import java.util.*
import kotlin.concurrent.timer

class BreakActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityBreakBinding
    private val utils = Utils()

    private var timerTask: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityBreakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //객체 받아옴
        val time = intent.getIntExtra("Break", -1)
        val set = intent.getIntExtra("Set", -1)

        //값이 안 넘어왔을 경우
        if(time == -1 || set == -1) {
            Log.e(TAG, "There is no time value or setting value.")
            utils.makeToast(this, "다시 시도해 주세요")
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

        if(isFinish)
            setResult(RESULT_OK)
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_break_stop -> stopTimeTask(false)
        }
    }
}