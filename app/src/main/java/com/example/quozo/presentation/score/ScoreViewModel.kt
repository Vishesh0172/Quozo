package com.example.quozo.presentation.score

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.quozo.data.room.QuizDao
import com.example.quozo.presentation.navigation.ScoreRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    quizDao: QuizDao
): ViewModel() {

    private val quizId = savedStateHandle.toRoute<ScoreRoute>().quizId
    private val _state = MutableStateFlow(ScoreState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch{
            val quiz = quizDao.getQuiz(quizId)
            _state.update { it.copy(
                score = quiz.score,
                date = quiz.date,
                category = quiz.category,
                difficulty = quiz.difficulty,
                questions = quiz.questionIds.size.toString()
            )}
        }

    }
}

data class ScoreState(
    val score:Int = 0,
    val date:String = "",
    val category: String = "",
    val difficulty: String = "",
    val questions: String = "",
)