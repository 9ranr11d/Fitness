package com.example.fitness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitness.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavi()
    }

    //Bottom navigation
    private fun setBottomNavi() {
        supportFragmentManager.beginTransaction().add(R.id.frame_lay_main_fragment, TimerFragment()).commit()
        binding.bNaviMain.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when(it.itemId) {
                R.id.item_b_navi_timer -> supportFragmentManager.beginTransaction().replace(R.id.frame_lay_main_fragment, TimerFragment()).commit()
                R.id.item_b_navi_search -> supportFragmentManager.beginTransaction().replace(R.id.frame_lay_main_fragment, SearchFragment()).commit()
                R.id.item_b_navi_calendar -> supportFragmentManager.beginTransaction().replace(R.id.frame_lay_main_fragment, CalendarFragment()).commit()
            }
            true
        })
    }
}