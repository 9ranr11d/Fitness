package com.example.fitness

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TrainingRecord::class],
    version = 1,
    exportSchema = false
)
abstract class DataBase: RoomDatabase() {
    abstract fun TrainingRecordDAO(): TrainingRecordDAO
}