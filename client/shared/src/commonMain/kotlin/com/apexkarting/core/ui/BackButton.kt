package com.apexkarting.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.uikit.icons.ApexIcon
import com.apexkarting.uikit.icons.Back
import com.apexkarting.uikit.icons.Icons

internal enum class BackButtonStyle {
    Offset,
    Floating,
}

@Composable
internal fun ApexBackButton(
    onClick: () -> Unit,
    style: BackButtonStyle = BackButtonStyle.Offset,
    modifier: Modifier = Modifier,
    floatingIconSize: Dp = ApexTheme.tokens.sizing.headerIconSize,
) {
    when (style) {
        BackButtonStyle.Offset -> ApexIcon(
            imageVector = Icons.Back,
            contentDescription = "Назад",
            modifier = modifier
                .offset(x = ApexTheme.tokens.spacing.md, y = ApexTheme.tokens.sizing.backButtonY)
                .clickable { onClick() },
            tint = MaterialTheme.colorScheme.onSurface,
            size = ApexTheme.tokens.sizing.headerIconSize,
        )

        BackButtonStyle.Floating -> ApexHeaderIconButton(
            imageVector = Icons.Back,
            contentDescription = "Назад",
            onClick = onClick,
            modifier = modifier,
            iconSize = floatingIconSize,
        )
    }
}