package com.example.fitness

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow

class RecordListViewModel(private val trainingRecordDAO: TrainingRecordDAO): ViewModel() {
    fun allRecord(): Flow<List<TrainingRecord>> = trainingRecordDAO.getAll()
}

class RecordListViewModelFactory(private val trainingRecordDAO: TrainingRecordDAO): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecordListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordListViewModel(trainingRecordDAO) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }
}