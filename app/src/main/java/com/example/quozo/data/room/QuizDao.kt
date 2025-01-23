package com.example.quozo.data.room

import androidx.room.Dao
import androidx.room.Delete
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

    @Query("UPDATE quiz SET score = :score WHERE quizId = :quizId")
    suspend fun updateScore(quizId: Long, score: Int)

    @Query("SELECT score from quiz WHERE quizId == :quizId")
    suspend fun getScore(quizId: Long) : Int

    @Query("UPDATE quiz SET status = 1 WHERE quizId = :quizId")
    suspend fun updateStatus(quizId: Long)

    @Query("SELECT * from quiz WHERE status == 1")
    suspend fun getCompletedQuiz() : List<Quiz>

    @Query("SELECT * from quiz WHERE status == 0")
    suspend fun getIncompleteQuiz() : List<Quiz>

    @Query("SELECT * from quiz")
    suspend fun getAllQuiz() : List<Quiz>

    @Delete
    suspend fun deleteQuiz(quiz: Quiz)

}