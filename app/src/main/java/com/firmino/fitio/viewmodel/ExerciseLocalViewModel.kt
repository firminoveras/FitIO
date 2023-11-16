package com.firmino.fitio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.firmino.fitio.model.entity.ExerciseLocal
import com.firmino.fitio.model.repository.ExerciseRepository
import kotlinx.coroutines.launch

class ExerciseLocalViewModel(
    private val repository: ExerciseRepository
) : ViewModel() {
    private val _allExercises: LiveData<List<ExerciseLocal>> = repository.allExercises.asLiveData()

    val allExercises: LiveData<List<ExerciseLocal>>
        get() = _allExercises

    fun insert(name: String, type: String, muscle: String, equipment: String, difficulty: String, instructions: String, sets: Int, reps: Int) = viewModelScope.launch {
        val input = ExerciseLocal(
            name = name,
            type = type,
            muscle = muscle,
            equipment = equipment,
            difficulty = difficulty,
            instructions = instructions,
            mon = false,
            tue = false,
            wed = false,
            thu = false,
            fri = false,
            sat = false,
            sun = false,
            reps = reps,
            sets = sets
        )
        repository.insert(input)
    }

    fun delete(name: String) = viewModelScope.launch {
        repository.delete(name)
    }

    fun update(exercise: ExerciseLocal) = viewModelScope.launch {
        repository.update(exercise)
    }

    fun resetAllSets() = viewModelScope.launch {
        repository.resetAllSets()
    }
}
