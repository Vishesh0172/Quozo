package com.example.quozo.di

import android.content.Context
import androidx.room.Room
import com.example.quozo.data.room.LocalQuizDatabase
import com.example.quozo.data.room.QuizDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): LocalQuizDatabase{
        return Room.databaseBuilder(
            context,
            LocalQuizDatabase::class.java, "local-quiz.db"
        ).build()
    }

    @Provides
    fun provideDao(database: LocalQuizDatabase): QuizDao{
        return database.quizDao()
    }
}