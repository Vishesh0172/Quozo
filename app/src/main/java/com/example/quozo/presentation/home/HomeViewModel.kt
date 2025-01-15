package com.example.quozo.presentation.home

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quozo.R
import com.example.quozo.data.local.CategoryDatabase
import com.example.quozo.data.local.DataStoreRepository
import com.example.quozo.models.QuizCategory
import com.example.quozo.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    categoryDatabase: CategoryDatabase,
    dataStoreRepository: DataStoreRepository
): ViewModel() {

    private val user = dataStoreRepository.userFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), User())
    private val _state = MutableStateFlow(HomeState(categoryList = categoryDatabase.categoryList))
    val state = combine(_state, user){state, user ->
        state.copy(userName = user.name, avatar = user.avatarId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState())

}

data class HomeState(
    val categoryList: List<QuizCategory> = emptyList(),
    val userName: String = "",
    @DrawableRes val avatar: Int = R.drawable.ic_launcher_foreground
)