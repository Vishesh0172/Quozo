package com.example.quozo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Question(
    val category: String,
    val id: String,
    @SerialName("question")
    val question: QuestionObj,
    val type: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
)


@Serializable
data class QuestionObj(
    @SerialName("text")
    val text: String
)
