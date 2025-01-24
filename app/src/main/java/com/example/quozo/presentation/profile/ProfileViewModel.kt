package com.example.quozo.presentation.profile

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quozo.R
import com.example.quozo.data.local.CategoryDatabase
import com.example.quozo.data.local.DataStoreRepository
import com.example.quozo.data.room.Quiz
import com.example.quozo.data.room.QuizDao
import com.example.quozo.models.QuizCategory
import com.example.quozo.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val quizDao: QuizDao,
    categoryDatabase: CategoryDatabase,
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    private val user = dataStoreRepository.userFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), User())
    private val _state = MutableStateFlow(ProfileState())

    val state = combine(_state, user) { state, user ->
        state.copy(userName = user.name, avatar = user.avatarId)
    }.onStart {
        _state.update { it.copy(
            incompleteList = quizDao.getIncompleteQuiz().toMutableStateList(),
            completeList = quizDao.getCompletedQuiz().toMutableStateList(),
            categoryList = categoryDatabase.categoryList
        ) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileState())

    fun onEvent(event: ProfileEvent){
        when(event){
            is ProfileEvent.UpdateAvatar -> {
                viewModelScope.launch{
                    dataStoreRepository.updateAvatar(event.id)
                }

            }
            is ProfileEvent.UpdateUserName -> {
                viewModelScope.launch{
                    dataStoreRepository.updateUserName(event.name)
                }

            }

            is ProfileEvent.DeleteQuiz -> {
                viewModelScope.launch{
                    quizDao.deleteQuiz(state.value.quizToDelete!!)
                    _state.update { it.copy(
                        incompleteList = state.value.incompleteList.also {
                            if (it.contains(state.value.quizToDelete))
                                it.remove(state.value.quizToDelete)
                        },

                        completeList = state.value.completeList.also {
                            if (it.contains(state.value.quizToDelete))
                                it.remove(state.value.quizToDelete)
                        }
                    ) }
                }
            }

            is ProfileEvent.ShowDialog -> {
                _state.update { it.copy(quizToDelete = event.quiz) }
            }

            ProfileEvent.DismissDialog -> {
                _state.update { it.copy(quizToDelete = null) }
            }
        }
    }
}

data class ProfileState(
    val userName: String = "",
    @DrawableRes val avatar: Int = R.drawable.ic_launcher_background,
    val incompleteList: SnapshotStateList<Quiz> = mutableStateListOf(),
    val completeList: SnapshotStateList<Quiz> = mutableStateListOf(),
    val categoryList: List<QuizCategory> = emptyList(),
    val avatarList: List<Int> = listOf(R.drawable.avatar_male, R.drawable.avatar_female),
    val quizToDelete: Quiz? = null
)

sealed interface ProfileEvent{
    data class UpdateAvatar(val id: Int): ProfileEvent
    data object DeleteQuiz: ProfileEvent
    data class UpdateUserName(val name: String): ProfileEvent
    data class ShowDialog(val quiz: Quiz): ProfileEvent
    data object DismissDialog: ProfileEvent
}