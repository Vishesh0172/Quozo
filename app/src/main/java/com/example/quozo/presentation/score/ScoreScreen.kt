package com.example.quozo.presentation.score

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ScoreScreen(modifier: Modifier = Modifier,state: ScoreState) {

    var showScore by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }
    val transition = updateTransition(targetState = scale)



    LaunchedEffect(state.score) {
        showScore = true
        delay(1000)
        scale.animateTo(0.75f, tween(800))
        showDetails = true
        delay(500)
        showButton = true
    }


    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){

//        (
//            targetState = scale,
//            transitionSpec = {
//                slideInVertically() togetherWith ExitTransition.None
//            }
//        ) { scale ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale.value)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .fillMaxSize()


            ) {

                AnimatedVisibility(
                    visible = showScore,
                    enter = slideInVertically(tween(800)){it}
                ) {
                    Column(
                        modifier = Modifier.animateContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.padding(26.dp),
                            text = state.score.toString(),
                            fontSize = 130.sp
                        )

                        AnimatedVisibility(
                            visible = showDetails,
                            enter = slideInVertically(tween(800)){it}
                        ) {
                            Column {
                                Text("Date: ${state.date}")
                                Text("Difficulty: ${state.difficulty}")
                                Text("Questions: ${state.questions}")
                                Text("Category: ${state.category}")
                            }
                        }
                    }
                }

            //}
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = showButton,
            enter = slideInVertically(tween(600)){it}
        ) {
            ElevatedButton(onClick = {}) { Text(text = "Continue")}
        }
    }
}
