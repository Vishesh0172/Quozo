package com.example.quozo.presentation.createQuiz

import android.view.MotionEvent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quozo.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CreateQuizScreen(
    modifier: Modifier = Modifier,
    state: CreateQuizState,
    onEvent:(CreateQuizEvent) -> Unit,
    navigateToQuiz:(Long) -> Unit,
    imgRes: Int,
    animatedVisibilityScope: AnimatedVisibilityScope
) {



    LaunchedEffect(state.quizId) {
        if (state.quizId!=null)
            navigateToQuiz(state.quizId)
    }




    var dropDownState by remember {  mutableStateOf(false) }


    if (state.showDialog){

        AlertDialog(
            onDismissRequest = {onEvent(CreateQuizEvent.DismissDialog)},
            confirmButton = { Button(onClick = {onEvent(CreateQuizEvent.DismissDialog)}) { Text(text = "Confirm")} },
            title = {Text(text = state.errorMessage!!)}
        )
    }

    Column (modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
            Image(
                modifier = Modifier
                    .fillMaxSize(0.6f)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "img/$imgRes"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .weight(1f),
                painter = painterResource(imgRes),
                contentDescription = null
            )
            Column(modifier = Modifier
                .weight(2.5f)
                .padding(start = 14.dp)) {
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

                Spacer(modifier = Modifier.height(30.dp))

                Text(text = "Time Limit", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(0.5f))

                Spacer(Modifier.height(36.dp))
                TimeLimit(time = if (state.timeLimit == 5) "05" else state.timeLimit.toString(), onEvent = onEvent)

            }

        AnimatedStartButton(onEvent = onEvent, buttonEnabled = state.buttonEnabled, modifier = Modifier.align(Alignment.CenterHorizontally))

    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimeLimit(modifier: Modifier = Modifier, time: String, onEvent: (CreateQuizEvent) -> Unit) {

    var oldTime by remember { mutableStateOf(time) }

    SideEffect {
        oldTime = time
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){

            Button(
                shape = CircleShape,
                onClick = { onEvent(CreateQuizEvent.MinusTimeLimit) },
                modifier = Modifier
                    .padding(8.dp)
                    .size(30.dp),
                contentPadding = PaddingValues(5.dp)
            ) {
                Icon(painter = painterResource(R.drawable.baseline_horizontal_rule_24), contentDescription = null)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                for (i in time.indices) {
                    var char = ""
                    char = if (oldTime[i] == time[i]) {
                        oldTime[i].toString()
                    }else
                        time[i].toString()
                    AnimatedContent(
                        targetState = char,
                        transitionSpec = {
                            if (time.toInt() > oldTime.toInt())
                                slideInVertically { it } togetherWith slideOutVertically { -it }
                            else
                                slideInVertically { -it } togetherWith slideOutVertically { it }
                        }, label = ""
                    ) { char ->
                        Text(
                            text = char,
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier,
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }



            Button(shape = CircleShape, onClick = {onEvent(CreateQuizEvent.PlusTimeLimit)}, modifier = Modifier.padding(10.dp).size(30.dp), contentPadding = PaddingValues(5.dp)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }

        Text("In Seconds", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 10.dp))
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AnimatedStartButton(modifier: Modifier = Modifier, onEvent: (CreateQuizEvent) -> Unit, buttonEnabled: Boolean) {

    val color by  animateColorAsState(targetValue =
    if(buttonEnabled == false)
        Color.Gray
    else
        MaterialTheme.colorScheme.tertiaryContainer, label = ""
    )

    val selected = remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if(selected.value) 0.9f else 1f, label = "")


    ElevatedButton(
        shape = RoundedCornerShape(8.dp),
        enabled = buttonEnabled,
        colors = ButtonDefaults.elevatedButtonColors(containerColor = color, contentColor = MaterialTheme.colorScheme.onTertiaryContainer),
        onClick = {

        },
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .scale(scale.value)
            .pointerInteropFilter {
                if (buttonEnabled) {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            selected.value = true
                        }

                        MotionEvent.ACTION_UP -> {
                            selected.value = false
                            onEvent(CreateQuizEvent.CreateQuiz)
                        }
                    }
                } else
                    selected.value = false
                true

            }
    ) {
        Text(text = "Start Quiz", style = MaterialTheme.typography.titleMedium)
    }

}
//@Preview
//@Composable
//fun CreateQuizPreview(modifier: Modifier = Modifier) {
//}