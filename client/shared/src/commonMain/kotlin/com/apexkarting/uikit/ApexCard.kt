package com.apexkarting.uikit

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import com.apexkarting.core.theme.ApexTheme

@Composable
fun ApexCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = ApexShapes.card(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val showHover = onClick != null && enabled && isHovered
    val hoverOverlay = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    val clickableModifier = if (onClick != null) {
        Modifier
            .hoverable(interactionSource = interactionSource, enabled = enabled)
            .apexClickable(
                shape = shape,
                enabled = enabled,
                interactionSource = interactionSource,
                onClick = onClick,
            )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(clickableModifier)
            .background(containerColor, shape),
    ) {
        if (showHover) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(hoverOverlay, shape),
            )
        }
        Column(
            modifier = Modifier.padding(ApexTheme.tokens.spacing.md),
            content = content,
        )
    }
}