package com.example.quozo.di

import android.content.Context
import com.example.quozo.data.local.CategoryDatabase
import com.example.quozo.data.local.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideCategoryDatabase(): CategoryDatabase = CategoryDatabase()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStoreRepository{
        return DataStoreRepository(context)
    }
}