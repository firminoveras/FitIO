package com.firmino.fitio.model.repository

import androidx.annotation.WorkerThread
import com.firmino.fitio.model.dao.ExerciseDao
import com.firmino.fitio.model.entity.ExerciseLocal
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {
    val allExercises: Flow<List<ExerciseLocal>> = exerciseDao.getAll()

    @WorkerThread
    suspend fun getByFilter(name: String, muscle: String, equipment: String, type: String) =
        exerciseDao.getByFilter(name, muscle, equipment, type)


    @WorkerThread
    suspend fun insert(exerciseLocal: ExerciseLocal) {
        exerciseDao.insert(exerciseLocal)
    }

    @WorkerThread
    suspend fun delete(name: String) {
        exerciseDao.delete(name)
    }

    @WorkerThread
    suspend fun update(exerciseLocal: ExerciseLocal) {
        exerciseDao.update(exerciseLocal)
    }

    @WorkerThread
    suspend fun resetAllSets() {
        exerciseDao.resetAllSets()
    }
}