package com.example.quozo.presentation.quiz

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.quozo.models.LoadingState

@Composable
fun QuizScreen(modifier: Modifier = Modifier, state: QuizState, onEvent:(QuizEvent) -> Unit, navigateToScore:(Int) -> Unit, onDialogDismiss:() -> Unit) {

    LaunchedEffect(state.quizComplete) {
        if (state.quizComplete == true)
            navigateToScore(state.score)
    }

    val buttonColor = animateColorAsState(
        animationSpec = tween(500),
        targetValue =
            when(state.answerState){
                AnswerState.NoAnswer -> Color.Unspecified
                else -> MaterialTheme.colorScheme.tertiaryContainer
            }
    ).value

    val animatedProgress = animateFloatAsState(
        targetValue = state.progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    val animatedTimerProgress = animateFloatAsState(
        targetValue = state.timerProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.height(10.dp),
            strokeCap = StrokeCap.Round,
            progress = {
                animatedProgress
            }
        )
        Spacer(modifier = Modifier.height(36.dp))
        when(state.loadingState) {
            LoadingState.Error -> AlertDialog(
                properties = DialogProperties(dismissOnClickOutside = false),
                modifier = modifier,
                onDismissRequest = { onDialogDismiss() },
                confirmButton = { Button(onClick = {onEvent(QuizEvent.Retry)}) { Text(text = "Retry") } },
                icon = { Icon(imageVector = Icons.Default.Warning, contentDescription = null) },
                title = {Text(text = "Couldn't fetch the Question")},
                text = {Text("Please Check Your Internet Connection and Try Again")}
            )
            LoadingState.Loading -> Box(modifier = modifier.fillMaxSize()){ CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))}

            LoadingState.Success -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.question,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Timer(
                            time = state.time,
                            progress = animatedTimerProgress,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))
                state.allOptions.forEach { option ->
                    OptionButton(
                        answerState = state.answerState,
                        option = option,
                        selectedAnswer = state.selectedAnswer,
                        color = Color.Green,
                        correctAnswer = state.correctAnswer,
                        onClick = {
                            onEvent(QuizEvent.SelectAnswer(option))
                            Log.d("SelectedAnswer", state.selectedAnswer)
                        }
                    )
                }


                Spacer(modifier = Modifier.height(25.dp))
                ElevatedButton(
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = buttonColor, contentColor = MaterialTheme.colorScheme.onTertiaryContainer),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    onClick = {
                        if (state.submitted == false)
                            onEvent(QuizEvent.SubmitAnswer)
                        else if (state.currentQuestionIndex == state.questionIds.size - 1)
                            onEvent(QuizEvent.QuizComplete)
                        else
                            onEvent(QuizEvent.NextQuestion)
                    }) {
                    Text(
                        style = MaterialTheme.typography.bodyMedium,
                        text =
                        if (state.submitted == false)
                            "Submit"
                        else if (state.currentQuestionIndex == state.questionIds.size - 1)
                            "Done"
                        else
                            "Next Question"
                    )
                }
            }
        }
    }


}

@Composable
fun OptionButton(modifier: Modifier = Modifier, option: String, color: Color, onClick:() -> Unit, selectedAnswer: String, answerState: AnswerState, correctAnswer: String) {

    val optionColor by animateColorAsState(
        animationSpec = tween(durationMillis = 500),
        targetValue =
        when(answerState){
            AnswerState.CorrectAnswer -> {
                if(selectedAnswer == option)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceContainer
            }
            AnswerState.NoAnswer -> MaterialTheme.colorScheme.surfaceContainer
            AnswerState.WrongAnswer -> {
                if(selectedAnswer == option)
                    MaterialTheme.colorScheme.errorContainer
                else if(option == correctAnswer)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceContainer
            }
        }
    )

    val optionBorder by animateColorAsState(
        if(selectedAnswer == option)
            MaterialTheme.colorScheme.tertiaryContainer
        else
            MaterialTheme.colorScheme.onSurface
    )


    val optionSelected = option == selectedAnswer



    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = if (optionSelected) 2.dp else 1.dp,
                shape = RoundedCornerShape(18.dp),
                color = optionBorder
            )
            .background(optionColor)
            .clickable(
                enabled = answerState == AnswerState.NoAnswer,
                onClick = {
                    onClick()
            }),

    ){
      Text(text = option, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun Timer(modifier: Modifier = Modifier, time: Int, progress: Float) {

    Box(modifier = modifier.size(50.dp), contentAlignment = Alignment.Center){
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            strokeWidth = 6.dp,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.fillMaxSize(),
            progress = { progress }
        )
        Text(text = time.toString())
    }


}

@Preview
@Composable
fun QuizScreenPreview(modifier: Modifier = Modifier) {
    val alloptions = listOf<String>("John Cena", "Vishu", "Randy")
    Surface {
        QuizScreen(
            state = QuizState(
                allOptions = alloptions,
                question = "Who is the GOAT of WWE",
                progress = 0.5f
            ),
            onEvent = {},
            onDialogDismiss = {},
            navigateToScore = {}
        )
    }
}