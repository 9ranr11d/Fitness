package com.example.fitness.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitness.view.model.TrainingRecordDAO

@Database(
    entities = [TrainingRecord::class],
    version = 1,
    exportSchema = false
)
abstract class DataBase: RoomDatabase() {
    abstract fun TrainingRecordDAO(): TrainingRecordDAO

    //ViewModel 위해 db 선언
    companion object {
        @Volatile
        private var INSTANCE: DataBase? = null

        fun getDatabase(context: Context): DataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    DataBase::class.java,
                    "TrainingRecord"
                )
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}