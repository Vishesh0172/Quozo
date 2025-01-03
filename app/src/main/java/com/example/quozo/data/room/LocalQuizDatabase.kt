package com.example.quozo.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Quiz::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LocalQuizDatabase: RoomDatabase() {
    abstract fun quizDao(): QuizDao
}