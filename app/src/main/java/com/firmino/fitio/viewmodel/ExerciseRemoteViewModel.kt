package com.firmino.fitio.viewmodel

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firmino.fitio.model.remote.ExerciseRemote
import com.firmino.fitio.model.remote.ExerciseService
import com.firmino.fitio.viewmodel.state.ExerciseRemoteState
import com.firmino.fitio.viewmodel.state.ExerciseRemoteState.Empty
import com.firmino.fitio.viewmodel.state.ExerciseRemoteState.Error
import com.firmino.fitio.viewmodel.state.ExerciseRemoteState.Loading
import com.firmino.fitio.viewmodel.state.ExerciseRemoteState.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExerciseRemoteViewModel : ViewModel() {
    private val service = ExerciseService()
    private val _state = MutableLiveData<ExerciseRemoteState>()

    val state: LiveData<ExerciseRemoteState>
        get() = _state

    fun getByFilters(name: String, muscle: String, difficulty: String, type: String) {
        _state.value = Loading
        viewModelScope.launch(Dispatchers.IO) {
            val res = service.getExerciseByFilters(name, muscle, difficulty, type)
            if (res.isSuccessful) {
                withContext(Dispatchers.Main) {
                    if (res.body().isNullOrEmpty()) {
                        _state.value = Empty
                    } else {
                        _state.value = Success(res.body()?.toSet()?.toList() ?: listOf())
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    _state.value = Error("Error")
                }
            }
        }
    }

    fun _getByFilters(name: String, muscle: String, difficulty: String, type: String) {
        _state.value = Loading
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _state.value = Success(
                    listOf(
                        ExerciseRemote("Teste 1", "strength", "biceps", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste 2", "strength", "biceps", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste 3", "strength", "lower_back", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste 4", "strength", "shoulders", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste 5", "strength", "shoulders", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste 6", "strength", "chest", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste 7", "strength", "biceps", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste 8", "strength", "biceps", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste 9", "strength", "chest", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste 0", "strength", "biceps", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                        ExerciseRemote("Teste A", "strength", "biceps", "dumbbell", "beginner", LoremIpsum(30).values.toString()),
                    )
                )
            }
        }
    }
}