package com.firmino.fitio.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firmino.fitio.model.repository.ExerciseRepository
import com.firmino.fitio.viewmodel.ExerciseLocalViewModel

class ExerciseViewModelFactory(private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseLocalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseLocalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
