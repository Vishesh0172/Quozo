package com.example.quozo.models

import androidx.annotation.DrawableRes
import com.example.quozo.R

data class User(
    val name: String = "",
    @DrawableRes val avatarId: Int = R.drawable.ic_launcher_foreground
)
