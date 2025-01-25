package com.example.quozo.presentation.home

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quozo.R
import com.example.quozo.models.QuizCategory

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    state: HomeState,
    modifier: Modifier = Modifier,
    createQuiz: (String, Int) -> Unit, onProfileClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    val categoryList = state.categoryList

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopBar(avatar = state.avatar, name = state.userName, onProfileClick = onProfileClick) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = rememberLazyGridState(),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 10.dp)
        ) {
            items(categoryList, key = {it.value}){
                QuizTypeCard(
                    modifier = Modifier.padding(
                        top = if (isOdd(categoryList.indexOf(it))) {
                            20.dp
                        } else {
                            0.dp
                        }
                    ).animateItem(),
                    category = it,
                    createQuiz = createQuiz,
                    animatedVisibilityScope = animatedVisibilityScope
                )

            }
        }
    }

}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.QuizTypeCard(
    modifier: Modifier = Modifier,
    category: QuizCategory,
    createQuiz:(String, Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    Box(modifier = modifier
        .aspectRatio(0.8f)
        .padding(horizontal = 8.dp)){
        ElevatedCard(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .align(Alignment.BottomCenter)){
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(10.dp), verticalArrangement = Arrangement.Bottom) {
                Text(text = category.displayName, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                PlayNowButton(category = category, createQuiz = createQuiz)
            }
        }

        Image(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "img/${category.imgRes}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
                .align(Alignment.TopStart),
            painter = painterResource(category.imgRes),
            contentDescription = null,
            alignment = Alignment.TopStart,
            contentScale = ContentScale.Fit
        )
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlayNowButton(modifier: Modifier = Modifier, createQuiz: (String, Int) -> Unit, category: QuizCategory) {

    val selected = remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if(selected.value) 0.8f else 1f, label = "")


    OutlinedButton(
        onClick = { },
        modifier = modifier
            .fillMaxWidth()
            .scale(scale.value)
            .pointerInteropFilter{
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        selected.value = true
                    }
                    MotionEvent.ACTION_UP -> {
                        selected.value = false
                        createQuiz(category.value, category.imgRes)
                    }
                }
                true

            }
    ) {
        Text(text = stringResource(R.string.play_now), style = MaterialTheme.typography.bodyMedium
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier, onProfileClick:() -> Unit, avatar: Int, name: String) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column(modifier = Modifier.fillMaxWidth(0.9f)){
                Text(text = stringResource(R.string.welcome), style = MaterialTheme.typography.titleSmall)
                Text(text = name, style = MaterialTheme.typography.titleLarge, overflow = TextOverflow.Ellipsis, maxLines = 1)
            }
        },
        actions = {
            IconButton(onClick = {onProfileClick()}) {
                Image(
                    painter = painterResource(avatar),
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
            ) }
        }
    )
}

private fun isOdd(index: Int): Boolean{
    return index>0 && index%2 != 0
}