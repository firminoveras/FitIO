package com.firmino.fitio.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.firmino.fitio.model.dao.ExerciseDao
import com.firmino.fitio.model.entity.ExerciseLocal

@Database(
    entities = [
        ExerciseLocal::class
    ],
    version = 5
)
abstract class ExerciseRoomDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: ExerciseRoomDatabase? = null

        fun getInstance(context: Context): ExerciseRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExerciseRoomDatabase::class.java,
                    "exercise_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}