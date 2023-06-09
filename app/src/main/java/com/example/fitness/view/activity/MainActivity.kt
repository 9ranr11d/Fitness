package com.example.fitness.view.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.fitness.R
import com.example.fitness.util.Utils
import com.example.fitness.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
//    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivityMainBinding
    private val utils = Utils()

    private lateinit var launcher: ActivityResultLauncher<Intent>

    companion object {
        const val utilFileName = "utilVar"
        var routine = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPrefTheme()
        initNaviGraph()
        setBtnListener()
        getLauncherResult()
    }

    //Theme 값 가져와서 적용
    private fun getPrefTheme() {
        val sharedPreferences = getSharedPreferences(utilFileName, MODE_PRIVATE)

        when(sharedPreferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
            AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    //Btn listener
    private fun setBtnListener() {
        binding.btnMainSetting.setOnClickListener {
            val goSetting = Intent(this, SettingActivity::class.java)

            launcher.launch(goSetting)
        }

    }

    //Bottom Navigation
    private fun initNaviGraph() {
        NavigationUI.setupWithNavController(binding.bNaviMain, findNavController(R.id.frag_main))
    }

    //Launcher Result 받아옴
    private fun getLauncherResult() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_CANCELED -> utils.makeToast(this, "취소되었습니다.")
            }
        }
    }
}