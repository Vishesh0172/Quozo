package com.example.quozo.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionId(
    @SerialName("id")
    val id: String
)