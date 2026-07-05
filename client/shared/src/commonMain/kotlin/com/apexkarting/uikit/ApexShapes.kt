package com.apexkarting.uikit

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import com.apexkarting.core.theme.ApexTheme

object ApexShapes {
    val circle: Shape
        @Composable get() = RoundedCornerShape(percent = 50)

    @Composable
    fun card(): Shape = RoundedCornerShape(ApexTheme.tokens.spacing.xl)

    @Composable
    fun chip(): Shape = RoundedCornerShape(ApexTheme.tokens.radius.pill)

    @Composable
    fun control(): Shape = RoundedCornerShape(ApexTheme.tokens.radius.lg)
}