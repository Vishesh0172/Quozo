package com.example.quozo.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quozo.models.QuizCategory

@Composable
fun HomeScreen(state: HomeState, modifier: Modifier = Modifier, paddingValues: PaddingValues, createQuiz: (String) -> Unit, onProfileClick: () -> Unit) {


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
            items(categoryList){
                QuizTypeCard(
                    modifier = Modifier.padding(
                        top = if (isOdd(categoryList.indexOf(it))){ 20.dp }else { 0.dp }
                    ),
                    category = it,
                    createQuiz = createQuiz,

                )
            }
        }
    }

}


@Composable
fun QuizTypeCard(modifier: Modifier = Modifier, category: QuizCategory, createQuiz:(String) -> Unit) {
    Box(modifier = modifier
        .aspectRatio(0.8f)
        .padding(horizontal = 8.dp)){
        ElevatedCard(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .align(Alignment.BottomCenter)){
            Column(Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.Bottom) {
                Text(text = category.displayName, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                OutlinedButton(onClick = {createQuiz(category.value)}, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Play Now", style = MaterialTheme.typography.bodyMedium
                    )
                   }
            }
        }

        Image(
            modifier = Modifier.align(Alignment.TopStart).fillMaxSize(0.6f),
            painter = painterResource(category.imgRes),
            contentDescription = null,
            alignment = Alignment.TopStart,
            contentScale = ContentScale.Fit
        )
    }

}

@Preview
@Composable
fun HomePreview(modifier: Modifier = Modifier, ) {
    //HomeScreen(paddingValues = PaddingValues(10.dp)){}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier, onProfileClick:() -> Unit, avatar: Int, name: String) {
    TopAppBar(
        title = {
            Column(){
                Text(text = "Welcome", style = MaterialTheme.typography.titleSmall)
                Text(text = name, style = MaterialTheme.typography.titleLarge)
            }
        },
        actions = {
            IconButton(onClick = {onProfileClick()}) {
                Image(
                    painter = painterResource(avatar),
                    modifier = Modifier.size(50.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
            ) }
        }
    )
}

private fun isOdd(index: Int): Boolean{
    if (index>0 && index%2 != 0){
        return true
    }
    return false
}