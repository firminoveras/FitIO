package com.firmino.fitio.model.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExerciseService {
    private val api: ExerciseApi = Retrofit.Builder()
        .baseUrl("https://api.api-ninjas.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ExerciseApi::class.java)

    suspend fun getExerciseByFilters(name: String, muscle: String, difficulty: String, type: String): Response<List<ExerciseRemote>> = api.getExerciseByFilter(name, muscle, difficulty, type)
}