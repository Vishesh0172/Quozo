package com.example.quozo.presentation.quiz

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
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

@Composable
fun QuizScreen(modifier: Modifier = Modifier, state: QuizState, onEvent:(QuizEvent) -> Unit, navigateToScore:(Int) -> Unit) {

    LaunchedEffect(state.quizComplete) {
        if (state.quizComplete == true)
            navigateToScore(state.score)
    }

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
            modifier = Modifier.height(10.dp),
            strokeCap = StrokeCap.Round,
            progress = {
                animatedProgress
            }
        )

        Spacer(modifier = Modifier.height(36.dp))
        Card(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(2f)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.question, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                Timer(time = state.time, progress = animatedTimerProgress, modifier = Modifier.align(Alignment.TopCenter))
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        state.allOptions.forEach { option ->
            OptionButton(
                option = option,
                selectedAnswer = state.selectedAnswer,
                color = Color.Green,
                onClick = {
                    onEvent(QuizEvent.SelectAnswer(option))
                    Log.d("SelectedAnswer", state.selectedAnswer)
                }
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        ElevatedButton(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            onClick = {
            if(state.submitted == false)
                onEvent(QuizEvent.SubmitAnswer)
            else if(state.currentQuestionIndex == state.questionIds.size - 1 )
                onEvent(QuizEvent.QuizComplete)
            else
                onEvent(QuizEvent.NextQuestion)
        }) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text =
                if(state.submitted == false)
                    "Submit"
                else if(state.currentQuestionIndex == state.questionIds.size - 1 )
                    "Done"
                else
                    "Next Question"
            )
        }
    }

}

@Composable
fun OptionButton(modifier: Modifier = Modifier, option: String, color: Color, onClick:() -> Unit, selectedAnswer: String) {

    val optionColor by animateColorAsState(
        if(selectedAnswer == option)
            Color.Green
        else
            MaterialTheme.colorScheme.secondary
    )

    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(5.dp)
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(18.dp),
                color = optionColor
            )
            .clickable(onClick = {
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
        QuizScreen(state = QuizState(allOptions = alloptions, question = "Who is the GOAT of WWE", progress = 0.5f), onEvent = {}) { }
    }
}