package com.example.quozo.presentation.score

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.example.quozo.presentation.navigation.ScoreRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ScoreViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val score = savedStateHandle.toRoute<ScoreRoute>().score
    private val _state = MutableStateFlow(ScoreState())
    val state = _state.asStateFlow()

    init {
        _state.update { it.copy(score = score)}
    }
}

data class ScoreState(
    val score:Int = 0
)