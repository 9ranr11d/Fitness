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

    @Query("DELETE FROM TrainingRecord WHERE part = :part")
    fun setDelPart(part: String)

    @Query("UPDATE TrainingRecord SET part = :toPart WHERE part = :fromPart")
    fun setUpdatePart(fromPart: String, toPart: String)
}