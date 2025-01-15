package com.example.quozo.presentation.home

import androidx.lifecycle.ViewModel
import com.example.quozo.data.local.CategoryDatabase
import com.example.quozo.models.QuizCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    categoryDatabase: CategoryDatabase
): ViewModel() {

    private val _state = MutableStateFlow(HomeState(categoryList = categoryDatabase.categoryList))
    val state = _state.asStateFlow()

}

data class HomeState(
    val categoryList: List<QuizCategory> = emptyList()
)