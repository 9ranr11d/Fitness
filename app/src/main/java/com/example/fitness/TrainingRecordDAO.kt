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

    @Query("SELECT * FROM TrainingRecord ORDER BY date ASC")
    fun getAllRecord(): Flow<List<TrainingRecord>>

    @Query("SELECT date, part FROM TrainingRecord ORDER BY date ASC")
    fun getAllDatePart(): Flow<List<DatePart>>
}