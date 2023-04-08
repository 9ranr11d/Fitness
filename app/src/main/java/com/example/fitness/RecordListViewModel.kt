package com.example.fitness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow

class RecordListViewModel(private val trainingRecordDAO: TrainingRecordDAO): ViewModel() {
    //모든 record 반환
    fun allRecord(): Flow<List<TrainingRecord>> = trainingRecordDAO.getAllRecord()


    //모든 record 중 date, part 반환
    fun allDatePart(): Flow<List<DatePart>> = trainingRecordDAO.getAllDatePart()

    fun delPart(part: String) = trainingRecordDAO.setDelPart(part)

    fun updatePart(fromPart: String, toPart: String) = trainingRecordDAO.setUpdatePart(fromPart, toPart)

    //Record 추가
    fun insertRecord(trainingRecord: TrainingRecord) = trainingRecordDAO.insertRecord(trainingRecord)

    //Record 삭제
    fun deleteRecord(trainingRecord: TrainingRecord) = trainingRecordDAO.deleteRecord(trainingRecord)

    //Record 수정
    fun updateRecord(trainingRecord: TrainingRecord) = trainingRecordDAO.updateRecord(trainingRecord)
}

class RecordListViewModelFactory(private val trainingRecordDAO: TrainingRecordDAO): ViewModelProvider.Factory {
    //ViewModelFactory
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecordListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordListViewModel(trainingRecordDAO) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }
}