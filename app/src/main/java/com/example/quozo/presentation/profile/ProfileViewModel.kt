package com.example.quozo.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quozo.data.local.CategoryDatabase
import com.example.quozo.data.room.Quiz
import com.example.quozo.data.room.QuizDao
import com.example.quozo.models.QuizCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    quizDao: QuizDao,
    categoryDatabase: CategoryDatabase
): ViewModel() {

    val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart { _state.update { it.copy(incompleteList = quizDao.getIncompleteQuiz(), completeList = quizDao.getCompletedQuiz(), categoryList = categoryDatabase.categoryList) }}
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileState())

}

data class ProfileState(
    val incompleteList: List<Quiz> = emptyList(),
    val completeList: List<Quiz> = emptyList(),
    val categoryList: List<QuizCategory> = emptyList()
)