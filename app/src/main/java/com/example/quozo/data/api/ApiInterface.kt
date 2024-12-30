package com.example.quozo.data.api

import com.example.quozo.models.QuestionId
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("questions")
    suspend fun getQuestionIds(): Response<List<QuestionId>>

}