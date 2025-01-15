package com.example.quozo.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val quizId: Int = 0,
    val category: String = "",
    val questionIds: List<String> = emptyList(),
    val questionsAnswered: Int = 0,
    val status: Boolean = false,
    val score: Int = 0,
    val timeLimit: Int = 0
)



