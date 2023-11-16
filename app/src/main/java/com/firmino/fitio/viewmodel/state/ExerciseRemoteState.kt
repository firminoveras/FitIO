package com.firmino.fitio.viewmodel.state

import com.firmino.fitio.model.remote.ExerciseRemote

sealed class ExerciseRemoteState {
    object Loading: ExerciseRemoteState()
    object Empty: ExerciseRemoteState()
    data class Success(val data: List<ExerciseRemote>): ExerciseRemoteState()
    data class Error(val message: String): ExerciseRemoteState()
}
