package com.example.quozo.presentation.createQuiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quozo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuizScreen(modifier: Modifier = Modifier, state: CreateQuizState, onEvent:(CreateQuizEvent) -> Unit, navigateToQuiz:(Long) -> Unit) {

    LaunchedEffect(state.quizId) {
        if (state.quizId!=null)
            navigateToQuiz(state.quizId)
    }


    var dropDownState by remember {  mutableStateOf(false) }
    val color by  animateColorAsState(targetValue =
        if(state.buttonEnabled == false)
            Color.Gray
        else
            MaterialTheme.colorScheme.tertiaryContainer, label = ""

    )

    Column (modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
            Image(modifier = Modifier.weight(1f), painter = painterResource(R.drawable.sports_icon2), contentDescription = null)
            Column(modifier = Modifier.weight(2.5f).padding(start = 14.dp)) {
                Text( text = "Let's Play", style = MaterialTheme.typography.displayLarge)
                Text( text = "Create Your Quiz", style = MaterialTheme.typography.titleSmall)

            }

        }
            Column(modifier = Modifier
                .weight(3f)
                .fillMaxSize()
                .padding(top = 30.dp, start = 12.dp, end = 12.dp)) {

                Text(text = "Questions", style = MaterialTheme.typography.titleSmall)
                TextField(
                    colors = TextFieldDefaults.colors(
                        errorIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    supportingText = {
                        if(state.buttonEnabled == false){
                            Text(
                                color = MaterialTheme.colorScheme.error,
                                text =
                                if(state.questionLimit <5)
                                    "Can't have less than 5 Questions"
                                else
                                    "Can't have more than 30 Questions"
                            )
                        }
                    },
                    isError = state.buttonEnabled,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
                    value = if(state.questionLimit == 0) "" else state.questionLimit.toString(),
                    onValueChange = { it ->
                        onEvent(
                            CreateQuizEvent.SetQuestionLimit(it)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Difficulty", style = MaterialTheme.typography.titleSmall)
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    expanded = dropDownState,
                    onExpandedChange = { dropDownState = it }) {
                    TextField(
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropDownState) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp)),
                        value = state.difficulty,
                        readOnly = true,
                        onValueChange = {}
                    )
                    ExposedDropdownMenu(
                        expanded = dropDownState,
                        onDismissRequest = { dropDownState = false }) {
                        val difficulties = listOf("Easy", "Medium", "Hard")
                        difficulties.forEach {
                            DropdownMenuItem(text = { Text(text = it) }, onClick = {
                                onEvent(CreateQuizEvent.Difficulty(it))
                                dropDownState = false
                            })
                        }
                    }


                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Time Limit", style = MaterialTheme.typography.titleSmall)
                TextField(
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium,
                    leadingIcon = {
                        IconButton(onClick = {onEvent(CreateQuizEvent.MinusTimeLimit)}, modifier = Modifier.weight(1f)) { Icon(imageVector = Icons.Default.Clear, contentDescription = null) }
                    },

                    trailingIcon = {
                        IconButton(onClick = { onEvent(CreateQuizEvent.PlusTimeLimit)}, modifier = Modifier.weight(1f)) { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
                    },
                    colors = TextFieldDefaults.colors(
                        errorIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    readOnly = true,
                    supportingText = {Text(text = "In Seconds", textAlign = TextAlign.Center) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxSize(0.4f)
                        .aspectRatio(1f)
                        .padding(top = 18.dp)
                        .align(Alignment.CenterHorizontally),
                    value = state.timeLimit.toString(),
                    onValueChange = { it ->
                        onEvent(
                            CreateQuizEvent.SetQuestionLimit(if (it == "") "0" else it)
                        )
                    }
                )
            }

            ElevatedButton(
                enabled = state.buttonEnabled,
                colors = ButtonDefaults.elevatedButtonColors(containerColor = color),
                onClick = { onEvent(CreateQuizEvent.CreateQuiz) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Start Quiz", style = MaterialTheme.typography.titleMedium)
            }
    }

}

@Preview
@Composable
fun CreateQuizPreview(modifier: Modifier = Modifier) {
    val state = CreateQuizState()
    Surface {
        CreateQuizScreen(state = state, navigateToQuiz = {}, onEvent = {})
    }
}