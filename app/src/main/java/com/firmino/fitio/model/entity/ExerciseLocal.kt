package com.firmino.fitio.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_table")
data class ExerciseLocal(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "muscle") val muscle: String,
    @ColumnInfo(name = "equipment") val equipment: String,
    @ColumnInfo(name = "difficulty") val difficulty: String,
    @ColumnInfo(name = "instructions") val instructions: String,
    @ColumnInfo(name = "mon") var mon: Boolean,
    @ColumnInfo(name = "tue") var tue: Boolean,
    @ColumnInfo(name = "wed") var wed: Boolean,
    @ColumnInfo(name = "thu") var thu: Boolean,
    @ColumnInfo(name = "fri") var fri: Boolean,
    @ColumnInfo(name = "sat") var sat: Boolean,
    @ColumnInfo(name = "sun") var sun: Boolean,
    @ColumnInfo(name = "sets") var sets: Int,
    @ColumnInfo(name = "reps") var reps: Int,
    @ColumnInfo(name = "setsCount") var setsCount: Int = 0,
)
