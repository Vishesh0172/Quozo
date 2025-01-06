package com.example.quozo.presentation.quiz

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.quozo.AppService
import com.example.quozo.data.api.ApiRepository
import com.example.quozo.data.room.QuizDao
import com.example.quozo.presentation.navigation.QuizRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
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
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
): ViewModel(){

    private val quizId = savedStateHandle.toRoute<QuizRoute>().quizId
    private val _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    init {
        val task = viewModelScope.launch{
            val quiz = quizDao.getQuiz(quizId)
            _state.update { it.copy(
                questionIds = quiz.questionIds,
                currentQuestionIndex = quiz.questionsAnswered,
                time = quiz.timeLimit,
                timeLimit = quiz.timeLimit
            ) }
            getQuestionAndUpdateState(quiz.questionIds[quiz.questionsAnswered])
        }
        Intent(context, AppService::class.java).also {
            it.action = AppService.Actions.START.toString()
            context.startService(it)
        }
        viewModelScope.launch{
            task.join()
            startTimer()

        }
    }



    fun onEvent(event: QuizEvent){
        when(event){
            QuizEvent.SubmitAnswer -> {

                Intent(context, AppService::class.java).also {
                    it.action = AppService.Actions.STOP.toString()
                    context.startService(it)
                }

                if(state.value.selectedAnswer == state.value.correctAnswer){
                    _state.update { it.copy(answerState = AnswerState.CorrectAnswer, score = state.value.score + 20) }
                }else
                    _state.update { it.copy(answerState = AnswerState.WrongAnswer) }

                _state.update { it.copy(
                    submitted = true,
                ) }

                viewModelScope.launch {
                    quizDao.updateQuestionsAnswered(quizId, state.value.currentQuestionIndex + 1)
                    quizDao.updateScore(quizId, state.value.score)
                }



            }

            QuizEvent.NextQuestion -> {
                val progress: Float = (state.value.currentQuestionIndex.toFloat()/state.value.questionIds.size.toFloat())
                _state.update { it.copy(
                    submitted = false,
                    selectedAnswer = "",
                    progress = progress,
                    currentQuestionIndex =
                    if(state.value.currentQuestionIndex == state.value.questionIds.size - 1)
                        state.value.currentQuestionIndex
                    else
                        state.value.currentQuestionIndex + 1,
                ) }

                viewModelScope.launch{
                    if(state.value.currentQuestionIndex <= state.value.questionIds.size - 1) {
                        val questionId = state.value.questionIds[state.value.currentQuestionIndex]
                        getQuestionAndUpdateState(questionId)
                        startTimer()
                    }
                }

            }

            is QuizEvent.SelectAnswer -> _state.update { it.copy(selectedAnswer = event.value) }

            QuizEvent.QuizComplete -> _state.update { it.copy(quizComplete = true) }
        }
    }



    private suspend fun startTimer(){
        while (state.value.time>=0){
            if (state.value.submitted == true) {
                Intent(context, AppService::class.java).also {
                    it.action = AppService.Actions.STOP.toString()
                    context.startService(it)
                }
                break
            }
            delay(1000L)
            _state.update { it.copy(time = state.value.time - 1) }
            _state.update { it.copy(timerProgress = state.value.time.toFloat() / state.value.timeLimit.toFloat()) }
            Intent(context, AppService::class.java).also {
                it.action = AppService.Actions.UPDATE.toString()
                it.putExtra("updatedValue", state.value.time.toString())
                context.startService(it)
            }
            Log.d("Timer", (state.value.time).toString())
            if (state.value.time == 0) {
                onEvent(QuizEvent.SubmitAnswer)
            }



        }
    }

    private suspend fun getQuestionAndUpdateState(id: String){

        val question = apiRepository.getQuestionById(id)
        val allOptions =  question.incorrectAnswers.toMutableList()
        allOptions.add(question.correctAnswer)
        val newList = allOptions.shuffled(Random)

        _state.update {
            it.copy(
                time = state.value.timeLimit,
                timerProgress = 1f,
                allOptions = newList,
                question = question.question.text,
                incorrectAnswers = question.incorrectAnswers,
                correctAnswer = question.correctAnswer,
            )
        }
    }

}

data class QuizState(
    val quizComplete: Boolean = false,
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
    val submitted: Boolean = false,
    val progress: Float = 0f,
    val timerProgress: Float = 1f
)

sealed interface AnswerState{
    data object NoAnswer: AnswerState
    data object CorrectAnswer: AnswerState
    data object WrongAnswer: AnswerState
}

sealed interface QuizEvent{
    data object SubmitAnswer: QuizEvent
    data object NextQuestion: QuizEvent
    data object QuizComplete: QuizEvent
    data class SelectAnswer(val value: String): QuizEvent
}