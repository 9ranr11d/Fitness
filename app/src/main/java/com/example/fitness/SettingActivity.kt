package com.example.fitness

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySettingBinding
    private val utils = Utils()

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecycler()
        setBtnListener()
        getLauncherResult()
    }

    //Btn Listener
    private fun setBtnListener() {
        binding.btnSettingBack.setOnClickListener(this)
    }

    //RecyclerView 설정
    private fun initRecycler() {
        binding.recyclerSettingMenu.layoutManager = LinearLayoutManager(this)

        val menuList = resources.getStringArray(R.array.setting_title)

        val settingListAdapter = SettingListAdapter(menuList) {
            when(it) {
                menuList[0] -> utils.makeToast(this, "테마")
                menuList[1] -> {
                    val goPartsAndColors = Intent(this, PartsAndColorsActivity::class.java)

                    launcher.launch(goPartsAndColors)
                }
            }
        }

        binding.recyclerSettingMenu.adapter = settingListAdapter
        binding.recyclerSettingMenu.addItemDecoration(DividerItemDecoration(this, 1))
    }

    //Launcher Result
    private fun getLauncherResult() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_CANCELED -> utils.makeToast(this, "취소되었습니다.")
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_setting_back -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}