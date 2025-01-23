package com.example.quozo.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.quozo.R

@Composable
fun CelebrationAnimation(modifier: Modifier = Modifier) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.celebration_animation))

    val progress by animateLottieCompositionAsState(
        composition = composition,
    )
    LottieAnimation(progress = {progress}, composition = composition, contentScale = ContentScale.Crop, modifier = modifier.fillMaxSize().alpha(0.8f),)

}