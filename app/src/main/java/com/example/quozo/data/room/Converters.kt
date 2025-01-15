package com.example.quozo.data.room

import androidx.room.TypeConverter

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