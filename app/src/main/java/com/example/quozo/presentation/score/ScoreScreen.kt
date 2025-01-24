package com.example.quozo.presentation.score

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quozo.R
import com.example.quozo.presentation.components.BackgroundAnimation
import kotlinx.coroutines.delay

@Composable
fun ScoreScreen(modifier: Modifier = Modifier,state: ScoreState, navigateUp:() -> Unit) {

    var showScore by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }



    LaunchedEffect(state.score) {
        showScore = true
        delay(1000)
        scale.animateTo(0.75f, tween(800))
        showDetails = true
        delay(500)
        showButton = true
    }


    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){

        BackgroundAnimation(modifier = Modifier.fillMaxSize())
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
                            fontSize = 130.sp,
                            fontFamily = FontFamily(Font(R.font.ubuntu_regular))
                        )


                        AnimatedVisibility(
                            visible = showDetails,
                            enter = slideInVertically(tween(800)){it}
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Difficulty: ${state.difficulty}", style = MaterialTheme.typography.bodyMedium)
                                Text("Questions: ${state.questions}", style = MaterialTheme.typography.bodyMedium)
                                Text("Category: ${state.category}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

            //}
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 32.dp, start = 12.dp, end = 12.dp),
            visible = showButton,
            enter = slideInVertically(tween(600)){it}
        ) {
            ElevatedButton(onClick = {navigateUp()}) { Text(text = "Continue")}
        }
    }
}
