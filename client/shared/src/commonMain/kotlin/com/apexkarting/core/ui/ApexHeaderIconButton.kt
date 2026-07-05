package com.apexkarting.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.uikit.ApexShapes
import com.apexkarting.uikit.apexClickable
import com.apexkarting.uikit.icons.ApexIcon

/** Иконка в шапке экрана: круглый hover, единый цвет с остальными header-кнопками. */
@Composable
internal fun ApexHeaderIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconSize: Dp = ApexTheme.tokens.sizing.headerIconSize,
) {
    val circleShape = ApexShapes.circle
    Box(
        modifier = modifier
            .size(ApexTheme.tokens.sizing.headerIconTouchSize)
            .apexClickable(circleShape, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ApexIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            size = iconSize,
        )
    }
}