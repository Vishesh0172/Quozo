package com.example.quozo.models

sealed interface LoadingState {

    data object Loading: LoadingState
    data object Error: LoadingState
    data object Success: LoadingState
}