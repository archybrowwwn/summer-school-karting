package com.apexkarting.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.apexkarting.core.theme.ApexTheme

@Composable
internal fun ListSkeletonCard(
    y: Dp,
    height: Dp = ApexTheme.tokens.sizing.listCardHeight,
) {
    Box(
        modifier = Modifier
            .width(ApexTheme.tokens.sizing.contentWidth)
            .height(height)
            .offset(x = ApexTheme.tokens.spacing.md, y = y)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
            ),
    )
}