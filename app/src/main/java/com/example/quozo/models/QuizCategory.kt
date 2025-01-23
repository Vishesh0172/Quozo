package com.example.quozo.models

import androidx.compose.runtime.Immutable


@Immutable
data class QuizCategory(
    val displayName: String,
    val value: String = displayName.lowercase(),
    val imgRes: Int
)
