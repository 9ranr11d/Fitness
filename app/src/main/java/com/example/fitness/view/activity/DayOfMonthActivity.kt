package com.example.fitness.view.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.R
import com.example.fitness.data.TrainingRecord
import com.example.fitness.databinding.ActivityDayOfMonthBinding
import com.example.fitness.util.Utils
import com.example.fitness.view.adapter.RecordListAdapter
import com.example.fitness.view.model.RecordListApplication
import com.example.fitness.view.model.RecordListViewModel
import com.example.fitness.view.model.RecordListViewModelFactory
import kotlinx.coroutines.launch

class DayOfMonthActivity : AppCompatActivity(), View.OnClickListener {
//    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityDayOfMonthBinding
    private val utils = Utils()

    private lateinit var date: String
    private lateinit var recordListAdapter: RecordListAdapter
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val recordListViewModel: RecordListViewModel by viewModels {
        RecordListViewModelFactory(
            (application as RecordListApplication).dataBase.TrainingRecordDAO()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityDayOfMonthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()
        initRecycler()
        setRecord()
        setBtnListener()
        getLauncherResult()
    }

    private fun setBtnListener() {
        binding.btnDayOfMonthBack.setOnClickListener(this)
        binding.btnDayOfMonthAdd.setOnClickListener(this)
    }

    private fun setRecord() {
        lifecycle.coroutineScope.launch {
            recordListViewModel.dateOfMonth(date).collect {
                recordListAdapter.submitList(it)
            }
        }
    }

    private fun initRecycler() {
        binding.recyclerDayOfMonthRecord.layoutManager = LinearLayoutManager(this)

        recordListAdapter = RecordListAdapter {
            val goOneRecord = Intent(this, OneRecordActivity::class.java)
            goOneRecord.putExtra("Target", it)
            goOneRecord.putExtra("Is_edit", true)

            launcher.launch(goOneRecord)
        }

        binding.recyclerDayOfMonthRecord.adapter = recordListAdapter
        binding.recyclerDayOfMonthRecord.addItemDecoration(DividerItemDecoration(this, 1))
    }

    private fun getData() {
        date = intent.getStringExtra("Date")!!

        binding.textDayOfMonthDate.text = date
    }

    //Launcher Result
    private fun getLauncherResult() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Utils.RESULT_INSERT -> utils.makeToast(this, "기록에 성공했습니다.")
                Utils.RESULT_UPDATE -> utils.makeToast(this, "기록 갱신에 성공했습니다.")
                Utils.RESULT_DELETE -> utils.makeToast(this, "기록 삭제에 성공했습니다.")
                Activity.RESULT_CANCELED -> utils.makeToast(this, "취소되었습니다.")
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_day_of_month_back -> {
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.btn_day_of_month_add -> {
                val addRecord = TrainingRecord(0, date, "00:00", "", "", 0, "", "")
                val goOneRecord = Intent(this, OneRecordActivity::class.java)
                goOneRecord.putExtra("Target", addRecord)
                goOneRecord.putExtra("Is_edit", false)

                launcher.launch(goOneRecord)
            }
        }
    }
}