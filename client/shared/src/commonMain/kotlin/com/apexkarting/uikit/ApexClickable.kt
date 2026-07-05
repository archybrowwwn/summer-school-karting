package com.apexkarting.uikit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role

@Composable
fun Modifier.apexClickable(
    shape: Shape,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onClick: () -> Unit,
): Modifier {
    return clip(shape)
        .clickable(
            enabled = enabled,
            role = Role.Button,
            interactionSource = interactionSource,
            indication = ripple(bounded = true),
            onClick = onClick,
        )
}