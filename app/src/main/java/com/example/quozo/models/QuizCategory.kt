package com.example.quozo.models

import androidx.annotation.DrawableRes

data class QuizCategory(
    val displayName: String,
    val value: String = displayName.lowercase(),
    @DrawableRes val imgRes: Int
)
