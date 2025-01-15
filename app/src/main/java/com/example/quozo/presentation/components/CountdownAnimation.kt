package com.example.quozo.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.quozo.R

@Composable
fun CountdownAnimation(modifier: Modifier = Modifier, animationComplete: (Boolean)-> Unit) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.countdown_3))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        
    )
    LottieAnimation(progress = {progress}, composition = composition, modifier = modifier.size(200.dp))

    if(progress == 1f){
        animationComplete(true)
    }

}

@Preview
@Composable
fun CountdownPreview(modifier: Modifier = Modifier) {
    Surface(modifier = Modifier.fillMaxSize()) {
        CountdownAnimation(Modifier.size(250.dp)) {  }
    }

}