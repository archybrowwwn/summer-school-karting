package com.apexkarting.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apexkarting.uikit.ApexShapes
import com.apexkarting.uikit.apexClickable
import com.apexkarting.uikit.icons.ApexIcon

@Composable
internal fun FloatingCircleIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp,
) {
    val circleShape = ApexShapes.circle
    Box(
        modifier = modifier
            .size(40.dp)
            .shadow(4.dp, circleShape)
            .apexClickable(circleShape, onClick = onClick)
            .background(MaterialTheme.colorScheme.surface, circleShape),
        contentAlignment = Alignment.Center,
    ) {
        ApexIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
            size = iconSize,
        )
    }
}