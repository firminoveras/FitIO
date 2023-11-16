package com.firmino.fitio

import android.app.Application
import com.firmino.fitio.model.ExerciseRoomDatabase
import com.firmino.fitio.model.repository.ExerciseRepository

class FitIoApplication : Application(){
    private val database by lazy { ExerciseRoomDatabase.getInstance(this)}
    val repository by lazy { ExerciseRepository(database.exerciseDao())}
}