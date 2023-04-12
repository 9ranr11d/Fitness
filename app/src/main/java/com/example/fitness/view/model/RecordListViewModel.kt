package com.example.fitness.view.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitness.data.DatePart
import com.example.fitness.data.TrainingRecord
import kotlinx.coroutines.flow.Flow

class RecordListViewModel(private val trainingRecordDAO: TrainingRecordDAO): ViewModel() {
    //모든, 날짜랑 부위만, 한 날짜의 Record 실시간으로 요구
    fun allRecord(): Flow<List<TrainingRecord>> = trainingRecordDAO.getAllRecord()
    fun allDatePart(): Flow<List<DatePart>> = trainingRecordDAO.getAllDatePart()
    fun dateOfMonth(date: String): Flow<List<TrainingRecord>> = trainingRecordDAO.getDayOfMonth(date)

    //부위 삭제
    fun delPart(part: String) = trainingRecordDAO.setDelPart(part)

    fun updatePart(fromPart: String, toPart: String) = trainingRecordDAO.setUpdatePart(fromPart, toPart)

    //Record 추가, 수정, 삭제
    fun insertRecord(trainingRecord: TrainingRecord) = trainingRecordDAO.insertRecord(trainingRecord)
    fun updateRecord(trainingRecord: TrainingRecord) = trainingRecordDAO.updateRecord(trainingRecord)
    fun deleteRecord(trainingRecord: TrainingRecord) = trainingRecordDAO.deleteRecord(trainingRecord)
}

//ViewModelFactory
class RecordListViewModelFactory(private val trainingRecordDAO: TrainingRecordDAO): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecordListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordListViewModel(trainingRecordDAO) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }
}