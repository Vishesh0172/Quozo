package com.example.quozo.data.local

import com.example.quozo.R
import com.example.quozo.models.QuizCategory

class CategoryDatabase {

    val categoryList = listOf<QuizCategory>(
        QuizCategory(displayName = "Sports", imgRes = R.drawable.sports_icon2),
        QuizCategory(displayName = "Film & TV", value = "film", imgRes = R.drawable.film_icon),
        QuizCategory(displayName = "Food & Drink", value = "food", imgRes = R.drawable.food_icon),
        QuizCategory(displayName = "General", value = "general_knowledge", imgRes = R.drawable.gk_icon),
        QuizCategory(displayName = "Geography", imgRes = R.drawable.geography_icon),
        QuizCategory(displayName = "History", imgRes = R.drawable.history_icon),
        QuizCategory(displayName = "Music", imgRes = R.drawable.music_icon),
        QuizCategory(displayName = "Science", imgRes = R.drawable.science_icon),
    )
}