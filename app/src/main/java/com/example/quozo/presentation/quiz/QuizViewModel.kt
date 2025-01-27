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
import com.example.quozo.data.room.Quiz
import com.example.quozo.data.room.QuizDao
import com.example.quozo.models.LoadingState
import com.example.quozo.presentation.navigation.QuizRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject
import kotlin.random.Random

private const val SCORE_INCREMENT = 50

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizDao: QuizDao,
    private val apiRepository: ApiRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
): ViewModel(){

    private val quizId = savedStateHandle.toRoute<QuizRoute>().quizId
    private lateinit var timerTask: Job
    private lateinit var quiz: Quiz
    private val _state = MutableStateFlow(QuizState(quizId = quizId))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch{
            quiz = quizDao.getQuiz(quizId)
            val progress: Float = (quiz.questionsAnswered.toFloat())/quiz.questionIds.size.toFloat()
            _state.update { it.copy(currentQuestionIndex = quiz.questionsAnswered, time = quiz.timeLimit, questionIds = quiz.questionIds, progress = progress) }
            getQuestionAndUpdateState(quiz.questionIds[quiz.questionsAnswered])
        }


    }



    fun onEvent(event: QuizEvent){
        when(event){
            QuizEvent.SubmitAnswer -> {

                timerTask.cancel()
                Log.d("timerTaskCancelled", timerTask.isCancelled.toString())
                Intent(context, AppService::class.java).also {
                    it.action = AppService.Actions.STOP.toString()
                    context.startService(it)
                }

                val progress: Float = (state.value.currentQuestionIndex.toFloat() + 1f)/quiz.questionIds.size.toFloat()
                _state.update { it.copy(submitted = true, progress = progress, buttonEnabled = true) }

                if(state.value.selectedAnswer == state.value.correctAnswer){
                    _state.update { it.copy(answerState = AnswerState.CorrectAnswer, score = state.value.score + SCORE_INCREMENT) }
                }else
                    _state.update { it.copy(answerState = AnswerState.WrongAnswer) }



                viewModelScope.launch {
                    quizDao.updateQuestionsAnswered(quizId, state.value.currentQuestionIndex + 1)
                    quizDao.updateScore(quizId, state.value.score)
                    if (state.value.currentQuestionIndex == quiz.questionIds.size - 1){
                        quizDao.updateStatus(quizId)
                    }
                }

            }

            QuizEvent.NextQuestion -> {
                _state.update { it.copy(
                    buttonEnabled = false,
                    submitted = false,
                    answerState = AnswerState.NoAnswer,
                    selectedAnswer = "",
                    loadingState = LoadingState.Loading,
                    currentQuestionIndex =
                    if(state.value.currentQuestionIndex == quiz.questionIds.size - 1)
                        state.value.currentQuestionIndex
                    else
                        state.value.currentQuestionIndex + 1,
                ) }

                viewModelScope.launch{
                    if(state.value.currentQuestionIndex <= quiz.questionIds.size - 1) {
                        val questionId = quiz.questionIds[state.value.currentQuestionIndex]
                        getQuestionAndUpdateState(questionId)
                    }
                }

            }

            is QuizEvent.SelectAnswer -> _state.update { it.copy(selectedAnswer = event.value, buttonEnabled = true) }

            QuizEvent.QuizComplete -> _state.update { it.copy(quizComplete = true, buttonEnabled = true) }
            QuizEvent.Retry -> viewModelScope.launch{
                _state.update { it.copy(loadingState = LoadingState.Loading) }
                val questionId = quiz.questionIds[state.value.currentQuestionIndex]
                getQuestionAndUpdateState(questionId)
            }
        }
    }


    private suspend fun startTimerFlow(){
        val timeLimit: Int =  quiz.timeLimit
        flow<Int> {
            for (i in timeLimit downTo 0) {
                emit(i)
                Intent(context, AppService::class.java).also {
                    it.action = AppService.Actions.START.toString()
                    it.putExtra("timerValue", i.toString())
                    context.startService(it)
                }
                Log.d("Timer Value emitted", i.toString())
                if (i == 0)
                    onEvent(QuizEvent.SubmitAnswer)
                else
                    delay(1000)
            }
        }
        .cancellable()
        .collect { value ->
            Log.d("Timer Value", value.toString())
            _state.update { it.copy(time = value, timerProgress = value.toFloat() / quiz.timeLimit.toFloat()) }
        }
    }

//    private suspend fun startTimer(){
//
//        var timerValue: Int =  quiz.timeLimit
//        var timerRunning = timerValue >= 0
//        while (state.value.submitted == false){
//            _state.update { it.copy(time = timerValue, timerProgress = timerValue.toFloat() / quiz.timeLimit.toFloat()) }
//            if (timerValue == 0) {
//                onEvent(QuizEvent.SubmitAnswer)
//                break
//            }
//            Intent(context, AppService::class.java).also {
//                it.action = AppService.Actions.START.toString()
//                it.putExtra("timerValue", timerValue.toString())
//                context.startService(it)
//            }
//            delay(1000L)
//            timerValue = timerValue - 1
//
//
//        }
//    }

    private suspend fun getQuestionAndUpdateState(id: String){

        try {
            val question = apiRepository.getQuestionById(id)
            val allOptions =  question.incorrectAnswers.toMutableList()
            allOptions.add(question.correctAnswer)
            val newList = allOptions.shuffled(Random)

            _state.update {
                it.copy(
                    buttonEnabled = false,
                    time = quiz.timeLimit,
                    timerProgress = 1f,
                    allOptions = newList,
                    question = question.question.text,
                    incorrectAnswers = question.incorrectAnswers,
                    correctAnswer = question.correctAnswer,
                    loadingState = LoadingState.Success,
                )
            }
            timerTask = viewModelScope.launch{startTimerFlow()}
        }catch (_: IOException){
            _state.update { it.copy(loadingState = LoadingState.Error) }
        }


    }

    override fun onCleared() {
        super.onCleared()
        Intent(context, AppService::class.java).also {
            it.action = AppService.Actions.STOP.toString()
            context.startService(it)
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
    val quizId: Long = 0L,
    val incorrectAnswers: List<String> = emptyList(),
    val correctAnswer: String = "",
    val question: String = "",
    val submitted: Boolean = false,
    val progress: Float = 0f,
    val timerProgress: Float = 1f,
    val loadingState: LoadingState = LoadingState.Loading,
    val buttonEnabled: Boolean = false
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
    data object Retry: QuizEvent
    data class SelectAnswer(val value: String): QuizEvent
}