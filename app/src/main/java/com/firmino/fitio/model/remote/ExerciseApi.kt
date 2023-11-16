package com.firmino.fitio.model.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ExerciseApi {
    @Headers("X-Api-Key: HI8fx2v0Sv5gcXdRM64KOSN3KNldYLCX0ES7uvNY")
    @GET("v1/exercises")
    suspend fun getExerciseByFilter(
        @Query("name") name: String,
        @Query("muscle") muscle: String,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String
    ): Response<List<ExerciseRemote>>
}