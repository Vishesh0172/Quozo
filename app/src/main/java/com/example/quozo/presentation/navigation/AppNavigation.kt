package com.example.quozo.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.quozo.presentation.createQuiz.CreateQuizScreen
import com.example.quozo.presentation.createQuiz.CreateQuizViewModel
import com.example.quozo.presentation.home.HomeScreen
import com.example.quozo.presentation.quiz.QuizScreen
import com.example.quozo.presentation.quiz.QuizViewModel
import kotlinx.serialization.Serializable

@Composable
fun AppNavigation(modifier: Modifier = Modifier, paddingValues: PaddingValues) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "MainNav"){

        navigation(route = "MainNav", startDestination = "HomeRoute"){
            composable(route = "HomeRoute"){
                HomeScreen(
                paddingValues = paddingValues,
                createQuiz = { navController.navigate(route = CreateQuizRoute(category = "Sports")) }
            ) }
            composable<CreateQuizRoute>{
                val args = it.toRoute<CreateQuizRoute>()
                val vm: CreateQuizViewModel = hiltViewModel()
                vm.init(args.category)
                val state by vm.state.collectAsState()
                CreateQuizScreen(
                    state = state,
                    onEvent = vm::onEvent,
                    modifier = Modifier.padding(paddingValues),
                    navigateToQuiz = { quizId ->
                        navController.navigate(QuizRoute(quizId = quizId))
                        Log.d("Quiz id from Navigation", quizId.toString())
                    }
                )
            }

            composable<QuizRoute>{
                val args = it.toRoute<QuizRoute>()
                val viewModel: QuizViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                QuizScreen(state = state, onEvent = viewModel::onEvent)
            }

        }

    }

}

@Serializable
data class CreateQuizRoute(
    val category: String
)

@Serializable
data class QuizRoute(
    val quizId: Long
)