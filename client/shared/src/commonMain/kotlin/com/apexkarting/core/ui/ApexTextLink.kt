package com.apexkarting.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.uikit.apexClickable

/** Текстовая ссылка с капсульным hover/ripple (овал под длину подписи). */
@Composable
internal fun ApexTextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(percent = 50),
) {
    Text(
        text = text,
        modifier = modifier
            .apexClickable(shape, onClick = onClick)
            .padding(
                horizontal = ApexTheme.tokens.spacing.sm,
                vertical = ApexTheme.tokens.spacing.xs,
            ),
        style = MaterialTheme.typography.bodyMedium,
        color = ApexTheme.colors.link,
    )
}