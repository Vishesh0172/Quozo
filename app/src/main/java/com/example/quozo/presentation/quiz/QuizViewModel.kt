package com.example.quozo.presentation.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.quozo.data.api.ApiRepository
import com.example.quozo.data.room.QuizDao
import com.example.quozo.presentation.navigation.QuizRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizDao: QuizDao,
    private val apiRepository: ApiRepository,
    savedStateHandle: SavedStateHandle
): ViewModel(){

    private val quizId = savedStateHandle.toRoute<QuizRoute>().quizId
    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch{
            val quiz = quizDao.getQuiz(quizId)
            _state.update { it.copy(questionIds = quiz.questionIds, currentQuestionIndex = quiz.questionsAnswered) }
            getQuestionAndUpdateState(quiz.questionIds[quiz.questionsAnswered])
        }
    }


    fun onEvent(event: QuizEvent){
        when(event){
            QuizEvent.SubmitAnswer -> {
                if(state.value.selectedAnswer == state.value.correctAnswer){
                    _state.update { it.copy(answerState = AnswerState.CorrectAnswer) }
                }else
                    _state.update { it.copy(answerState = AnswerState.WrongAnswer) }

                _state.update { it.copy(submitted = true) }
                viewModelScope.launch {
                    quizDao.updateQuestionsAnswered(quizId, state.value.currentQuestionIndex + 1)
                }
            }

            QuizEvent.NextQuestion -> {
                viewModelScope.launch{
                    _state.update { it.copy(currentQuestionIndex = state.value.currentQuestionIndex + 1) }
                    val questionId = state.value.questionIds[state.value.currentQuestionIndex]
                    getQuestionAndUpdateState(questionId)
                }
            }

            is QuizEvent.SelectAnswer -> _state.update { it.copy(selectedAnswer = event.value) }
        }
    }



    private suspend fun getQuestionAndUpdateState(id: String){

        val question = apiRepository.getQuestionById(id)
        val allOptions =  question.incorrectAnswers.toMutableList()
        allOptions.add(question.correctAnswer)
        val newList = allOptions.shuffled(Random)

        _state.update {
            it.copy(
                allOptions = newList,
                question = question.question.text,
                incorrectAnswers = question.incorrectAnswers,
                correctAnswer = question.correctAnswer
            )
        }
    }

}

data class QuizState(
    val selectedAnswer: String = "",
    val currentQuestionIndex: Int = 0,
    val allOptions: List<String> = emptyList(),
    val currentQuestionId: String = "",
    val questionIds: List<String> = emptyList(),
    val answerState: AnswerState = AnswerState.NoAnswer,
    val score: Int = 0,
    val time: Int = 0,
    val timeLimit: Int = 0,
    val incorrectAnswers: List<String> = emptyList(),
    val correctAnswer: String = "",
    val question: String = "",
    val submitted: Boolean = false
)

sealed interface AnswerState{
    data object NoAnswer: AnswerState
    data object CorrectAnswer: AnswerState
    data object WrongAnswer: AnswerState
}

sealed interface QuizEvent{
    data object SubmitAnswer: QuizEvent
    data object NextQuestion: QuizEvent
    data class SelectAnswer(val value: String): QuizEvent
}