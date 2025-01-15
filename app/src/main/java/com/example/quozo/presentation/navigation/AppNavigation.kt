package com.example.quozo.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.quozo.presentation.components.CountdownAnimation
import com.example.quozo.presentation.createQuiz.CreateQuizScreen
import com.example.quozo.presentation.createQuiz.CreateQuizViewModel
import com.example.quozo.presentation.home.HomeScreen
import com.example.quozo.presentation.home.HomeViewModel
import com.example.quozo.presentation.profile.ProfileScreen
import com.example.quozo.presentation.profile.ProfileViewModel
import com.example.quozo.presentation.quiz.QuizScreen
import com.example.quozo.presentation.quiz.QuizViewModel
import com.example.quozo.presentation.score.ScoreScreen
import com.example.quozo.presentation.score.ScoreViewModel
import kotlinx.serialization.Serializable

@Composable
fun AppNavigation(modifier: Modifier = Modifier, paddingValues: PaddingValues) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "MainNav"){

        navigation(route = "MainNav", startDestination = "HomeRoute"){
            composable(route = "HomeRoute"){
                val viewModel: HomeViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                HomeScreen(
                    paddingValues = paddingValues,
                    state = state,
                    onProfileClick = {navController.navigate("ProfileRoute")},
                    createQuiz = { category -> navController.navigate(route = CreateQuizRoute(category = category)) }
            ) }
            composable<CreateQuizRoute>{
                val viewModel: CreateQuizViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                CreateQuizScreen(
                    state = state,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.padding(paddingValues),
                    navigateToQuiz = { quizId ->
                        navController.navigate(AnimationRoute(quizId = quizId)){popUpTo<CreateQuizRoute>(){inclusive = true} }
                        Log.d("Quiz id from Navigation", quizId.toString())
                    }
                )
            }

            composable<AnimationRoute>(){
                val args = it.toRoute<AnimationRoute>()
                CountdownAnimation(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    navController.navigate(QuizRoute(args.quizId)){popUpTo<AnimationRoute>(){inclusive = true} } }
            }

            composable<QuizRoute>{
                        val viewModel: QuizViewModel = hiltViewModel()
                        val state by viewModel.state.collectAsStateWithLifecycle()
                        QuizScreen(
                            modifier = Modifier.padding(paddingValues),
                            state = state,
                            onEvent = viewModel::onEvent,
                            navigateToScore = { score ->
                                navController.navigate(ScoreRoute(score)) {
                                    popUpTo<QuizRoute>() { inclusive = true }
                                }
                            }
                        )
            }

            composable<ScoreRoute>{
                val viewModel: ScoreViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                ScoreScreen(state = state)
            }

            composable(route = "ProfileRoute"){
                val viewModel: ProfileViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                ProfileScreen(
                    continueQuiz =  {quizId ->
                        navController.navigate(AnimationRoute(quizId = quizId)){ }
                    },
                    state = state,
                    modifier = Modifier.padding(paddingValues),
                    onEvent = viewModel::onEvent
                )
            }

        }

        navigation(route = "ProfileNav", startDestination = "ProfileRoute"){

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

@Serializable
data class ScoreRoute(
    val score: Int
)

@Serializable
data class AnimationRoute(
    val quizId: Long
)