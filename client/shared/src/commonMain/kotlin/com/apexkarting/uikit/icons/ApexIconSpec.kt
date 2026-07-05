package com.apexkarting.uikit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.unit.dp

/** Единая спецификация line-иконок (Apple / SF Symbols). */
internal object ApexIconSpec {
    const val VIEWPORT = 24f
    val SIZE = 24.dp
    val SMALL_SIZE = 20.dp
    const val STROKE = 1.75f
    val STROKE_CAP = StrokeCap.Round
    val STROKE_JOIN = StrokeJoin.Round
    val STROKE_COLOR = SolidColor(Color.Black)
}