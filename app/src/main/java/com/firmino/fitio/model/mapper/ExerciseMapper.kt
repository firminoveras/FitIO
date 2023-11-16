package com.firmino.fitio.model.mapper

import com.firmino.fitio.model.entity.ExerciseLocal
import com.firmino.fitio.model.remote.ExerciseRemote

fun ExerciseRemote.toExercise() = Exercise(
    name = this.name,
    type = this.type,
    muscle = this.muscle,
    equipment = this.equipment,
    difficulty = this.difficulty,
    instructions = this.instructions,
)

fun ExerciseLocal.toExercise() = Exercise(
    name = this.name,
    type = this.type,
    muscle = this.muscle,
    equipment = this.equipment,
    difficulty = this.difficulty,
    instructions = this.instructions,
)

fun ExerciseLocal.updateByWeekdayList(list: List<Boolean>) : ExerciseLocal{
    if (list.size == 7){
        this.sun = list[0]
        this.mon = list[1]
        this.tue = list[2]
        this.wed = list[3]
        this.thu = list[4]
        this.fri = list[5]
        this.sat = list[6]
    }
    return this
}

fun ExerciseLocal.getWeekdayAsList() = listOf(
    this.sun,
    this.mon,
    this.tue,
    this.wed,
    this.thu,
    this.fri,
    this.sat,
)