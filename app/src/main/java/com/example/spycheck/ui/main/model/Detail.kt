package com.example.spycheck.ui.main.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class Detail(
    val id: String,
    val icon: ImageVector,
    @StringRes val title: Int,
    @StringRes val shortDescription: Int,
    @StringRes val longDescription: Int,
    val realLifeExamples: List<Int>,
    val hasInteractiveDemo: Boolean = false
)