package com.apexkarting.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.apexkarting.core.theme.ApexTheme

@Composable
internal fun ListSkeletonCard(
    modifier: Modifier = Modifier,
    height: Dp = ApexTheme.tokens.sizing.listCardHeight,
    y: Dp? = null,
) {
    val positionedModifier = if (y != null) {
        modifier.offset(y = y)
    } else {
        modifier
    }
    Box(
        modifier = positionedModifier
            .fillMaxWidth()
            .height(height)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
            ),
    )
}