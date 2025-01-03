package com.example.quozo.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface QuizDao {

    @Query("SELECT * from quiz WHERE quizId == :quizId")
    suspend fun getQuiz(quizId: Long) : Quiz

    @Upsert
    suspend fun upsertQuiz(quiz: Quiz) : Long

    @Query("UPDATE quiz SET questionsAnswered = :questionsAnswered WHERE quizId = :quizId")
    suspend fun updateQuestionsAnswered(quizId: Long, questionsAnswered: Int)
}