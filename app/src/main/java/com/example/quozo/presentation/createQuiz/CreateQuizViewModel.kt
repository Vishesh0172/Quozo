package com.example.quozo.presentation.createQuiz

import androidx.lifecycle.ViewModel
import com.example.quozo.data.api.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreateQuizViewModel @Inject constructor(
    val apiRepository: ApiRepository
): ViewModel(){



    private val _state = MutableStateFlow<CreateQuizState>(CreateQuizState())
    val state = _state.asStateFlow()

    fun init(category: String){
        _state.update { it.copy(category = category) }
    }


    fun onEvent(event: CreateQuizEvent){
        when(event) {
            is CreateQuizEvent.CreateQuiz -> {}
            is CreateQuizEvent.Difficulty -> _state.update { it.copy(difficulty = event.value) }
            is CreateQuizEvent.SetQuestionLimit -> {
                _state.update { it.copy(
                    questionLimit = if(event.value.length>3) state.value.questionLimit else event.value.toInt(),
                    buttonEnabled = if(event.value.toInt()>=5 && event.value.toInt()<=30)true else false)
                }
            }

            is CreateQuizEvent.MinusTimeLimit -> _state.update {
                val currentTimeLimit = state.value.timeLimit
                it.copy(
                    timeLimit = if(currentTimeLimit == 10) 10 else currentTimeLimit - 10
                ) }
            is CreateQuizEvent.PlusTimeLimit -> _state.update {
                val currentTimeLimit = state.value.timeLimit
                it.copy(
                    timeLimit = if(currentTimeLimit == 60) 60 else currentTimeLimit + 10
                ) }
        }
    }
}


data class CreateQuizState(
    val category: String = "",
    val questionLimit: Int = 0,
    val timeLimit: Int  = 10,
    val difficulty: String = "Medium",
    val buttonEnabled: Boolean = false
)

sealed interface CreateQuizEvent{
    data class SetQuestionLimit(val value: String): CreateQuizEvent
    data object PlusTimeLimit: CreateQuizEvent
    data object MinusTimeLimit: CreateQuizEvent
    data class Difficulty(val value: String): CreateQuizEvent
    data class CreateQuiz(
        val questionLimit: String,
        val difficulty: String
    ): CreateQuizEvent
}