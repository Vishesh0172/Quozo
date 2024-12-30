package com.example.quozo.data.api

import com.example.quozo.models.QuestionId
import retrofit2.Response
import javax.inject.Inject


class ApiRepository @Inject constructor(
    private val apiInterface: ApiInterface
) {

    suspend fun getQuestionIds(): List<QuestionId>?{
        val response = apiInterface.getQuestionIds()
        if (response.isSuccessful){
            return response.body()!!
        }
        return null
    }
}