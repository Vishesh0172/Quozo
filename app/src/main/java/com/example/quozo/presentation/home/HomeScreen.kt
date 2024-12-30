package com.example.quozo.presentation.home

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quozo.R
import kotlin.collections.mapOf

@Composable
fun HomeScreen(modifier: Modifier = Modifier, paddingValues: PaddingValues, createQuiz: () -> Unit) {


    val map = mapOf("Sports" to R.drawable.sports_icon2, "Film" to R.drawable.film_icon, "Maths" to R.drawable.math_icon)//("Sports", "Film", "Maths")
    val keyList = map.keys.toList()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopBar() }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = rememberLazyGridState(),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 10.dp)
        ) {
            items(keyList){
                QuizTypeCard(
                    modifier = Modifier.padding(
                        top =
                        if (isOdd(keyList.indexOf(it))){
                            20.dp
                        }else {
                            0.dp
                        }
                    ),
                    title = it,
                    createQuiz = createQuiz,
                    icon = map.getValue(it)
                )
            }
        }
    }

}


@Composable
fun QuizTypeCard(modifier: Modifier = Modifier, title: String, createQuiz:() -> Unit, @DrawableRes icon: Int) {
    Box(modifier = modifier
        .aspectRatio(0.8f)
        .padding(horizontal = 8.dp)){
        ElevatedCard(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .align(Alignment.BottomCenter)){
            Column(Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.Bottom) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = {createQuiz()}, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Play Now", style = MaterialTheme.typography.bodyMedium
                    )
                   }
            }
        }

        Image(
            modifier = Modifier.align(Alignment.TopStart).fillMaxSize(0.6f),
            painter = painterResource(icon),
            contentDescription = null,
            alignment = Alignment.TopStart,
            contentScale = ContentScale.Fit
        )
    }

}

@Preview
@Composable
fun HomePreview(modifier: Modifier = Modifier) {
    HomeScreen(paddingValues = PaddingValues(10.dp)){}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Column(){
                Text(text = "Welcome", style = MaterialTheme.typography.titleSmall)
                Text(text = "Vishesh", style = MaterialTheme.typography.titleLarge)
            }
        },
        actions = {
            IconButton(onClick ={} ) {
                Icon(
                imageVector = Icons.Filled.AccountCircle,
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