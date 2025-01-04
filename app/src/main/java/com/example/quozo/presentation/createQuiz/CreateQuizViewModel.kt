package com.example.quozo.presentation.createQuiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quozo.data.api.ApiRepository
import com.example.quozo.data.room.Quiz
import com.example.quozo.data.room.QuizDao
import com.example.quozo.data.room.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateQuizViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val dao: QuizDao
): ViewModel(){

    init {
        Log.d("DAO", "Dao Injected: $dao")
    }

    private val _state = MutableStateFlow<CreateQuizState>(CreateQuizState())
    private val _quizId = MutableStateFlow<Long?>(null)
    val state = combine(_state,_quizId){state, quizId ->
        state.copy(quizId = quizId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CreateQuizState())

    fun init(category: String){
        _state.update { it.copy(category = category) }
    }


    fun onEvent(event: CreateQuizEvent){
        when(event) {
            is CreateQuizEvent.CreateQuiz -> {
                viewModelScope.launch{

                    val questionIds = apiRepository.getQuestionIds(
                        category = state.value.category.lowercase(),
                        difficulty = state.value.difficulty.lowercase(),
                        limit = state.value.questionLimit
                    )

                    val quiz = Quiz(
                        category = state.value.category,
                        questionIds = questionIds,
                        questionsAnswered = 0,
                        status = Status.INCOMPLETE.value,
                        score = 0,
                        timeLimit = state.value.timeLimit
                    )
                    val quizId = dao.upsertQuiz(quiz)
                    _quizId.update { quizId }
                }
            }

            is CreateQuizEvent.Difficulty -> _state.update { it.copy(difficulty = event.value) }
            is CreateQuizEvent.SetQuestionLimit -> {
                _state.update { it.copy(
                    questionLimit =
                    if(event.value == "")
                        0
                    else {
                        if (event.value.length > 3)
                            state.value.questionLimit
                        else
                            event.value.toInt()
                    },)
                }
                _state.update { it.copy(buttonEnabled = if(state.value.questionLimit>=5 && state.value.questionLimit<=30)true else false) }
            }

            is CreateQuizEvent.MinusTimeLimit -> _state.update {
                val currentTimeLimit = state.value.timeLimit
                it.copy(
                    timeLimit = if(currentTimeLimit == 5) 5 else currentTimeLimit - 5
                ) }
            is CreateQuizEvent.PlusTimeLimit -> _state.update {
                val currentTimeLimit = state.value.timeLimit
                it.copy(
                    timeLimit = if(currentTimeLimit == 60) 60 else currentTimeLimit + 5
                ) }
        }
    }
}


data class CreateQuizState(
    val quizId: Long? = null,
    val category: String = "",
    val questionLimit: Int = 5,
    val timeLimit: Int  = 10,
    val difficulty: String = "Medium",
    val buttonEnabled: Boolean = true
)

sealed interface CreateQuizEvent{
    data class SetQuestionLimit(val value: String): CreateQuizEvent
    data object PlusTimeLimit: CreateQuizEvent
    data object MinusTimeLimit: CreateQuizEvent
    data class Difficulty(val value: String): CreateQuizEvent
    data object CreateQuiz: CreateQuizEvent
}