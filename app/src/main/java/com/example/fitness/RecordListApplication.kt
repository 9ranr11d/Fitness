package com.example.fitness

import android.app.Application

class RecordListApplication: Application() {
    val dataBase: DataBase by lazy { DataBase.getDatabase(this)}
}