package com.example.fitness.view.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.R
import com.example.fitness.view.adapter.SettingListAdapter
import com.example.fitness.util.Utils
import com.example.fitness.databinding.ActivitySettingBinding
import com.example.fitness.databinding.DialogThemeBinding

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = javaClass.simpleName
    private lateinit var binding: ActivitySettingBinding
    private lateinit var themeBinding: DialogThemeBinding
    private val utils = Utils()

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivitySettingBinding.inflate(layoutInflater)
        themeBinding = DialogThemeBinding.inflate(layoutInflater)
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
                menuList[0] -> {
                    val themeLay = themeBinding.root
                    setThemeLay()

                    val themeDialog = utils.initDialog(this, "테마")
                        ?.setView(themeLay)
                        ?.setNegativeButton("취소") { dialog, _ ->
                            dialog.dismiss()
                            utils.makeToast(this, "취소되었습니다.")

                            if(themeLay.parent != null)
                                (themeLay.parent as ViewGroup).removeView(themeLay)
                        }
                        ?.create()

                    themeDialog?.show()
                }
                menuList[1] -> {
                    val goPartsAndColors = Intent(this, PartsAndColorsActivity::class.java)

                    launcher.launch(goPartsAndColors)
                }
            }
        }

        binding.recyclerSettingMenu.adapter = settingListAdapter
        binding.recyclerSettingMenu.addItemDecoration(DividerItemDecoration(this, 1))
    }

    //Theme 설정
    private fun setThemeLay() {
        //저장되어 있던 Theme 값으로 체크
        val sharedPreferences = getSharedPreferences(MainActivity.utilFileName, MODE_PRIVATE)
        when(sharedPreferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
            AppCompatDelegate.MODE_NIGHT_NO -> themeBinding.gRadioTheme.check(R.id.b_radio_theme_light)
            AppCompatDelegate.MODE_NIGHT_YES -> themeBinding.gRadioTheme.check(R.id.b_radio_theme_dark)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> themeBinding.gRadioTheme.check(R.id.b_radio_theme_default)
        }

        themeBinding.bRadioThemeLight.setOnClickListener(this)
        themeBinding.bRadioThemeDark.setOnClickListener(this)
        themeBinding.bRadioThemeDefault.setOnClickListener(this)
    }

    //Launcher Result
    private fun getLauncherResult() {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(it.resultCode) {
                Activity.RESULT_CANCELED -> utils.makeToast(this, "저장되었습니다..")
            }
        }
    }

    //Theme 설정
    private fun setPrefTheme(theme: Int) {
        val sharedPreferences = getSharedPreferences(MainActivity.utilFileName, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("Theme", theme)
        editor.apply()

        Log.d(TAG, "Theme Num is $theme")

        when(theme) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_setting_back -> {
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.b_radio_theme_light -> setPrefTheme(AppCompatDelegate.MODE_NIGHT_NO)
            R.id.b_radio_theme_dark -> setPrefTheme(AppCompatDelegate.MODE_NIGHT_YES)
            R.id.b_radio_theme_default -> setPrefTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}