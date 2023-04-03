package com.example.fitness

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingRecordDAO {
    @Insert
    fun insertRecord(trainingRecord: TrainingRecord)

    @Update
    fun updateRecord(trainingRecord: TrainingRecord)

    @Delete
    fun deleteRecord(trainingRecord: TrainingRecord)

    @Query("SELECT * from TrainingRecord ORDER BY date ASC")
    fun getAll(): Flow<List<TrainingRecord>>
}