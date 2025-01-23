package com.example.quozo.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quozo.data.local.DataStoreRepository
import com.example.quozo.models.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NameViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {

    private val _state = MutableStateFlow(NameState())
    private val user = dataStoreRepository.userFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val state = combine(_state, user){state, user ->
        Log.d("User", user?.name.toString())
        if (user == null){
            state.copy()
        }else{
            state.copy(userCreated = user.name.isNotEmpty(), loadingState = if(user.name.isNotEmpty()) LoadingState.Loading else LoadingState.Success )
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NameState())



    fun onEvent(event: NameEvent){
        when(event) {
            NameEvent.SubmitName -> {
                viewModelScope.launch {
                    dataStoreRepository.updateUserName(state.value.name)
                    _state.update { it.copy(userCreated = true) }
                }
            }

            is NameEvent.TypeName -> {
                _state.update {
                    it.copy(
                        name = if (event.value.length > 15)
                            state.value.name
                        else
                            event.value
                    )
                }
                _state.update { it.copy(buttonEnabled = if (state.value.name.length >= 2 && state.value.name.length <= 15) true else false) }
        }

        }
    }
}

data class NameState(
    val loadingState: LoadingState = LoadingState.Loading,
    val userCreated: Boolean = false,
    val name: String = "",
    val buttonEnabled: Boolean = false
)

sealed interface NameEvent{
    data class TypeName(val value: String): NameEvent
    data object SubmitName: NameEvent
}