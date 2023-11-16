package com.firmino.fitio.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.firmino.fitio.model.entity.ExerciseLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercise_table")
    fun getAll(): Flow<List<ExerciseLocal>>

    @Query(
        "SELECT * FROM exercise_table WHERE " +
        "name LIKE '%' || :name || '%' " +
        "AND muscle LIKE '%' || :muscle || '%' " +
        "AND equipment LIKE '%' || :equipment || '%' " +
        "AND difficulty LIKE '%' || :difficulty || '%'"
    )
    suspend fun getByFilter(name: String, muscle: String, equipment:String, difficulty: String): List<ExerciseLocal>

    @Insert(onConflict = REPLACE)
    suspend fun insert(exerciseLocal: ExerciseLocal)

    @Query("DELETE FROM exercise_table WHERE name = :name")
    suspend fun delete(name: String)

    @Update(onConflict = REPLACE)
    suspend fun update(exerciseLocal: ExerciseLocal)

    @Query("UPDATE exercise_table SET setsCount = 0")
    suspend fun resetAllSets()
}