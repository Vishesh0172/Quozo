package com.example.quozo.data.api

import android.util.Log
import com.example.quozo.models.Question
import javax.inject.Inject


class ApiRepository @Inject constructor(
    private val apiInterface: ApiInterface
) {
    suspend fun getQuestionIds(
        category: String,
        difficulty: String,
        limit: Int
    ): List<String>{
        val response = apiInterface.getQuestionIds(category = category, difficulty = difficulty, questionLimit = limit)
        val idList: MutableList<String> = mutableListOf()
        Log.d("Api Response", response.body().toString())
        if (response.isSuccessful){
            response.body()!!.forEach{
                idList.add(it.id)
                Log.d("Api Question id", it.id)
            }
        }
        return idList
    }

    suspend fun getQuestionById(id: String): Question{
        Log.d("Question Id ApiRepository", id)
        val response = apiInterface.getQuestionById(id)
        Log.d("Question Response", response.toString())
        return response
    }
}