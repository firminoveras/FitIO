package com.firmino.fitio.viewmodel.state

import com.firmino.fitio.model.entity.ExerciseLocal

sealed class ExerciseLocalState {
    object Loading: ExerciseLocalState()
    object Empty: ExerciseLocalState()
    data class Success(val data: List<ExerciseLocal>): ExerciseLocalState()
    data class Error(val message: String): ExerciseLocalState()
}
