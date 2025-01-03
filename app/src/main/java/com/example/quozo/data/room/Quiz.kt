package com.example.quozo.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity
data class Quiz(
    @PrimaryKey(autoGenerate = true)
    val quizId: Int = 0,
    val category: String,
    val questionIds: List<String>,
    val questionsAnswered: Int = 0,
    val status: Boolean,
    val score: Int,
    val timeLimit: Int
)

enum class Status(val value: Boolean){
    COMPLETE(true),
    INCOMPLETE(false)
}

class Converters{
    @TypeConverter
    fun listToString(list: List<String>): String{
        return list.joinToString(",")
    }

    @TypeConverter
    fun stringToList(string: String): List<String>{
        return string.split(",")
    }
}