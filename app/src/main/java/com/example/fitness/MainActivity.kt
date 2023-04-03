package com.example.fitness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.fitness.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val utilFileName = "utilVar"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNaviGraph()
    }

    //Bottom Navigation
    private fun initNaviGraph() {
        NavigationUI.setupWithNavController(binding.bNaviMain, findNavController(R.id.frag_main))
    }
}