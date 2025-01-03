package com.example.quozo.data.api

import com.example.quozo.models.Question
import com.example.quozo.models.QuestionId
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("questions")
    suspend fun getQuestionIds(
        @Query("limit")questionLimit: Int,
        @Query("categories")category: String,
        @Query("difficulties")difficulty: String,
    ): Response<List<QuestionId>>

    @GET("question/{id}")
    suspend fun getQuestionById(@Path("id")id: String): Question

}