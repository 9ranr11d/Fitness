package com.example.fitness

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingRecordDAO {
    @Insert
    fun insertRecord(trainingRecord: TrainingRecord)

    @Delete
    fun deleteRecord(trainingRecord: TrainingRecord)

    @Query("SELECT * from TrainingRecord")
    fun selectAll(): Flow<List<TrainingRecord>>
}