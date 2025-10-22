package com.example.spycheck.ui.main.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class Detail(
    val id: String,
    @StringRes val title: Int,
    @StringRes val shortDescription: Int,
    val icon: ImageVector,
    @StringRes val longDescription: Int,
    val realLifeExamples: List<Int>
)
