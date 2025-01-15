package com.example.quozo.presentation.profile

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.quozo.R
import com.example.quozo.data.room.Quiz
import com.example.quozo.data.room.Status
import com.example.quozo.models.QuizCategory
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, state: ProfileState, continueQuiz: (Long) -> Unit, onEvent:(ProfileEvent) -> Unit) {

    var selectedTabIndex by remember { mutableStateOf(0) }
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    val titles = listOf<String>("Incomplete", "Complete")
    val pagerState = rememberPagerState() { 2 }
    val coroutineScope = rememberCoroutineScope()

    if (showAvatarDialog)
        AvatarDialog(
            modifier = modifier,
            avatarList = state.avatarList,
            updateAvatar = onEvent,
            onDismiss = {showAvatarDialog = false}
        )

    if(showNameDialog)
        UserNameDialog(
            modifier = modifier,
            name = state.userName,
            onDismiss = {showNameDialog = false},
            onEvent = onEvent
        )

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(state.avatar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable(onClick = { showAvatarDialog = true })
            )

            Row (verticalAlignment = Alignment.CenterVertically){
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = state.userName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 12.dp).weight(1f)
                )
                IconButton(onClick = {showNameDialog = true}) { Icon(imageVector = Icons.Rounded.Edit, contentDescription = null) }
            }
        }


        Spacer(Modifier.height(24.dp))

        TabRow(selectedTabIndex = selectedTabIndex, containerColor = MaterialTheme.colorScheme.background) {
           titles.forEachIndexed { index, title ->
               Tab(
                   text = { Text(text = title) },
                   selected = selectedTabIndex == index,
                   onClick = {
                      coroutineScope.launch{ pagerState.animateScrollToPage(index)}
                   }
               )
           }
        }

        HorizontalPager(state = pagerState) { page ->

            when(page){
                0 -> {
                    QuizList(modifier = Modifier.fillMaxSize(),items = state.incompleteList, categoryList = state.categoryList, continueQuiz = continueQuiz)
                    selectedTabIndex = 0
                }
                1 -> {
                    QuizList(modifier = Modifier.fillMaxSize(),items = state.completeList, categoryList = state.categoryList, continueQuiz)
                    selectedTabIndex = 1
                }
            }


        }
    }



}


@Composable
fun QuizList(modifier: Modifier = Modifier, items: List<Quiz>, categoryList: List<QuizCategory>, continueQuiz: (Long) -> Unit) {

    LazyColumn (modifier = modifier, state = rememberLazyListState()){
        items(items){ quiz ->
            val category = categoryList.find { it.value.equals(quiz.category, ignoreCase = true)  }
            val imgId = category?.imgRes ?: R.drawable.ic_launcher_foreground
            val displayName = category?.displayName!!
            QuizItem(quiz = quiz, imgId = imgId, modifier = Modifier.padding(10.dp), displayName = displayName, continueQuiz = continueQuiz)
        }
    }
}

@Composable
fun QuizItem(modifier: Modifier = Modifier, quiz: Quiz, @DrawableRes imgId: Int, displayName: String, continueQuiz:(Long) -> Unit) {

    val progressFloat = quiz.questionsAnswered.toFloat()/quiz.questionIds.size.toFloat()
    var expanded by remember { mutableStateOf(false) }
    val animatedProgress = animateFloatAsState(
        targetValue = progressFloat,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    val quizComplete = quiz.status == Status.COMPLETE.value

    Card (
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()

    ){
        Column (
            modifier = Modifier
                .wrapContentHeight()
                .clickable(onClick = { expanded = !expanded })
                .padding(10.dp)
                .animateContentSize()
        ){

        Row (modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(20.dp)
                    .weight(2f)
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "Played on 12/12/12", style = MaterialTheme.typography.bodySmall)
                if (quiz.status == Status.INCOMPLETE.value)
                    LinearProgressIndicator(
                        trackColor = MaterialTheme.colorScheme.secondaryContainer,
                        color = MaterialTheme.colorScheme.secondary,
                        progress = { animatedProgress },
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .height(8.5.dp)
                    )
                else
                    AssistChip(
                        onClick = {},
                        label = { Text(text = "Complete") }
                    )


            }

            Image(
                painter = painterResource(imgId),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .size(75.dp)
                    .align(Alignment.CenterVertically)
            )
        }
            if (expanded) {
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){

                        OutlinedButton(
                            modifier = Modifier.wrapContentHeight(),
                            onClick = {},
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        ) { Text(text = "Delete") }

                        OutlinedButton(
                            modifier = Modifier.wrapContentHeight(),
                            onClick = {
                                if (!quizComplete){
                                    continueQuiz(quiz.quizId.toLong())
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(),
                        ) { Text(text = if (quizComplete) "View Details" else "Continue" ) }
                }

            }

        }
    }

}

@Composable
fun AvatarDialog(modifier: Modifier = Modifier, avatarList: List<Int>, updateAvatar:(ProfileEvent) -> Unit, onDismiss:() -> Unit) {

    var selectedImage by remember { mutableStateOf(avatarList[0]) }

    Dialog(onDismissRequest = {onDismiss()}) {

        Card(modifier = modifier
            .fillMaxSize(0.75f)
            .aspectRatio(1f)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                Image( painter = painterResource(selectedImage), contentDescription = null, modifier = Modifier.weight(4f))
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly){
                    avatarList.forEach { avatarId ->
                        val imageSelected = avatarId == selectedImage
                        Image(
                            painter = painterResource(avatarId),
                            contentDescription = null,
                            modifier = Modifier
                                .border(
                                    shape = CircleShape,
                                    width = if (imageSelected) 2.dp else 0.dp,
                                    color = if (imageSelected) MaterialTheme.colorScheme.outline else Color.Transparent
                                )
                                .clickable(onClick = { selectedImage = avatarId })
                        )
                    }
                }
                Button(onClick = {
                    updateAvatar(ProfileEvent.updateAvatar(selectedImage))
                    onDismiss()
                }, 
                    modifier = Modifier.padding(10.dp)
                ) { Text(text = "Update Avatar") }

            }
        }
    }

}

@Composable
fun UserNameDialog(modifier: Modifier = Modifier, name: String, onDismiss: () -> Unit, onEvent: (ProfileEvent) -> Unit) {

    var nameState by remember { mutableStateOf(name) }

    Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            modifier = modifier
                .fillMaxSize(0.75f)
                .aspectRatio(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    shape = RoundedCornerShape(20.dp),
                    value = nameState,
                    onValueChange = { nameState = it },
                    singleLine = true,
                    modifier = Modifier.padding(10.dp))
                Button(
                    onClick = {
                        onEvent(ProfileEvent.updateUserName(nameState))
                        onDismiss()
                    },
                    modifier = Modifier.padding(10.dp)
                ) { Text(text = "Update") }

            }
        }

    }
}
@Preview
@Composable
fun QuizItemPreview(modifier: Modifier = Modifier) {
    val questionList = listOf<String>("", "")
    Surface {
        QuizItem(
            quiz = Quiz(category = "Sports", questionsAnswered = 1, questionIds = questionList),
            imgId = R.drawable.sports_icon2,
            displayName = ""
        ){}
    }
}