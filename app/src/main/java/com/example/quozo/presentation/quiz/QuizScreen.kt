package com.example.quozo.presentation.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun QuizScreen(modifier: Modifier = Modifier, state: QuizState, onEvent:(QuizEvent) -> Unit) {

    Column {

        Text(text = state.score.toString())
        Text(text = state.question)
        state.allOptions.forEach { option ->
            Button(onClick = {onEvent(QuizEvent.SelectAnswer(option))}) { Text(text = option) }
        }

        Button(onClick = {
            if(state.submitted == false)
                onEvent(QuizEvent.SubmitAnswer)
            else
                onEvent(QuizEvent.NextQuestion)
        }) {
            Text(
                text =
                if(state.submitted == true)
                    "Next Question"
                else
                    "Submit"
            )
        }
    }

}