package com.example.quozo.presentation.navigation

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import com.example.quozo.presentation.onboarding.NameScreen
import com.example.quozo.presentation.onboarding.NameViewModel
import com.example.quozo.presentation.profile.ProfileScreen
import com.example.quozo.presentation.profile.ProfileViewModel
import com.example.quozo.presentation.quiz.QuizScreen
import com.example.quozo.presentation.quiz.QuizViewModel
import com.example.quozo.presentation.score.ScoreScreen
import com.example.quozo.presentation.score.ScoreViewModel
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(modifier: Modifier = Modifier, paddingValues: PaddingValues) {

    val navController = rememberNavController()

    SharedTransitionLayout{
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = "OnBoardingNav",
        ) {

            navigation(route = "OnBoardingNav", startDestination = "NameRoute"){
                composable(route = "NameRoute"){
                    val viewModel : NameViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    NameScreen(
                        modifier = Modifier.padding(paddingValues),
                        state = state,
                        onEvent = viewModel::onEvent,
                        navigateToMain = {
                            if (state.userCreated)
                                navController.navigate("MainNav"){
                                    popUpTo("NameRoute"){inclusive = true}
                                    launchSingleTop = true
                                }
                        }
                    )
                }
            }

            navigation(route = "MainNav", startDestination = "HomeRoute") {
                composable(
                    route = "HomeRoute",
                    enterTransition = {
                        if (initialState.destination.route == "NameRoute"){
                            slideInVertically() + fadeIn()
                        }else
                            slideInHorizontally(initialOffsetX = { -it })
                                      },
                    exitTransition = {
                        slideOutHorizontally(targetOffsetX = { -it })
                    }
                ) {
                    val viewModel: HomeViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    HomeScreen(
                        state = state,
                        onProfileClick = { navController.navigate("ProfileRoute") },
                        createQuiz = { category, img ->
                            navController.navigate(
                                route = CreateQuizRoute(
                                    category = category,
                                    imgRes = img
                                )
                            )
                        },
                        animatedVisibilityScope = this,
                    )
                }
                composable<CreateQuizRoute>(
                    enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
                ) {
                    val args = it.toRoute<CreateQuizRoute>()
                    val viewModel: CreateQuizViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    CreateQuizScreen(
                        state = state,
                        onEvent = viewModel::onEvent,
                        modifier = Modifier.padding(paddingValues),
                        navigateToQuiz = { quizId ->
                            navController.navigate(AnimationRoute(quizId = quizId)) {
                                popUpTo<CreateQuizRoute> {
                                    inclusive = true
                                }

                            }
                            Log.d("Quiz id from Navigation", quizId.toString())
                        },
                        imgRes = args.imgRes,
                        animatedVisibilityScope = this
                    )
                }

                composable<AnimationRoute> {
                    val args = it.toRoute<AnimationRoute>()
                    CountdownAnimation(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        navController.navigate(QuizRoute(args.quizId)) {
                            popUpTo<AnimationRoute> {
                                inclusive = true
                            }
                        }
                    }
                }

                composable<QuizRoute> {
                    val viewModel: QuizViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    QuizScreen(
                        modifier = Modifier.padding(paddingValues),
                        state = state,
                        onEvent = viewModel::onEvent,
                        navigateToScore = { score ->
                            navController.navigate(ScoreRoute(state.quizId)) {
                                popUpTo<QuizRoute> { inclusive = true }
                            }
                        },
                        onDialogDismiss = {
                            navController.navigateUp()
                        }
                    )
                }

                composable<ScoreRoute> {
                    val viewModel: ScoreViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    ScoreScreen(state = state, modifier = Modifier.padding(paddingValues), navigateUp = {navController.navigateUp()})
                }

                composable(
                    route = "ProfileRoute",
                    enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
                ) {
                    val viewModel: ProfileViewModel = hiltViewModel()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    ProfileScreen(
                        continueQuiz = { quizId ->
                            navController.navigate(AnimationRoute(quizId = quizId)) { }
                        },
                        state = state,
                        modifier = Modifier.padding(paddingValues),
                        onEvent = viewModel::onEvent,
                        viewDetails = {quizId ->
                            navController.navigate(ScoreRoute(quizId = quizId)) { }
                        }
                    )
                }

            }

            navigation(route = "ProfileNav", startDestination = "ProfileRoute") {


            }

        }
    }

}

@Serializable
data class CreateQuizRoute(
    val category: String,
    @DrawableRes val imgRes: Int
)

@Serializable
data class QuizRoute(
    val quizId: Long
)

@Serializable
data class ScoreRoute(
    val quizId: Long
)

@Serializable
data class AnimationRoute(
    val quizId: Long
)