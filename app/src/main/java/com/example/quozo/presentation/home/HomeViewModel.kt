package com.example.quozo.presentation.home

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quozo.R
import com.example.quozo.data.local.CategoryDatabase
import com.example.quozo.data.local.DataStoreRepository
import com.example.quozo.models.LoadingState
import com.example.quozo.models.QuizCategory
import com.example.quozo.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    }.onStart { _state.update { it.copy(loadingState = LoadingState.Success, animateState = true) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState())

}

data class HomeState(
    val animateState: Boolean = false,
    val loadingState: LoadingState = LoadingState.Loading,
    val categoryList: List<QuizCategory> = emptyList(),
    val userName: String = "",
    @DrawableRes val avatar: Int = R.drawable.ic_launcher_foreground
)