package com.example.quozo.models

data class Question(
    val category: String,
    val id: String,
    val question: QuestionObj,
    val type: String,
    val correctAnswer: String,
    val incorrectAnswer: List<String>
)


data class QuestionObj(
    val text: String
)
