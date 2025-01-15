package com.example.quozo.di

import com.example.quozo.data.local.CategoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideCategoryDatabase(): CategoryDatabase = CategoryDatabase()
}