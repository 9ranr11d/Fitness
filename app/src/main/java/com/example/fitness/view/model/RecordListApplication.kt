package com.example.fitness.view.model

import android.app.Application
import com.example.fitness.data.DataBase

class RecordListApplication: Application() {
    val dataBase: DataBase by lazy { DataBase.getDatabase(this) }
}