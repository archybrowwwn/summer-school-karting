package com.apexkarting.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexPalette
import com.apexkarting.core.theme.ApexTheme

internal enum class StateArtwork {
    Empty,
    Error,
}

internal enum class ListStatePlacement {
    Overlay,
    TabContent,
}

@Composable
internal fun ListStateMessage(
    title: String,
    description: String,
    buttonText: String? = null,
    onClick: (() -> Unit)? = null,
    artwork: StateArtwork? = StateArtwork.Empty,
    placement: ListStatePlacement = ListStatePlacement.Overlay,
) {
    val illustrated = artwork != null
    val columnModifier = when (placement) {
        ListStatePlacement.Overlay -> Modifier
            .width(ApexTheme.tokens.sizing.contentWidth)
            .offset(
                x = ApexTheme.tokens.spacing.md,
                y = if (illustrated) 190.dp else ApexTheme.tokens.sizing.listStateMessageY,
            )
        ListStatePlacement.TabContent -> Modifier
            .contentWidthModifier()
            .padding(top = ApexTheme.tokens.spacing.xl)
    }
    Column(
        modifier = columnModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs),
    ) {
        if (illustrated) {
            StateIllustration(artwork)
            Spacer(Modifier.height(ApexTheme.tokens.spacing.md))
        }
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            style = if (illustrated) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.titleMedium
            },
            fontWeight = if (illustrated) FontWeight.Bold else null,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = description,
            modifier = if (illustrated) Modifier.fillMaxWidth() else Modifier,
            style = if (illustrated) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (buttonText != null && onClick != null) {
            if (illustrated) {
                Spacer(Modifier.height(ApexTheme.tokens.spacing.lg))
            }
            Button(
                onClick = onClick,
                modifier = if (illustrated) {
                    Modifier
                        .fillMaxWidth()
                        .height(ApexTheme.tokens.sizing.buttonHeight)
                } else {
                    Modifier
                },
                shape = if (illustrated) RoundedCornerShape(ApexTheme.tokens.radius.pill) else ButtonDefaults.shape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
private fun StateIllustration(artwork: StateArtwork) {
    val primary = MaterialTheme.colorScheme.primary
    Canvas(
        modifier = Modifier.size(width = 212.dp, height = 150.dp),
    ) {
        val light = Color(ApexPalette.IllustrationMuted)
        val basePath = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.82f)
            cubicTo(size.width * 0.02f, size.height * 0.46f, size.width * 0.28f, size.height * 0.03f, size.width * 0.52f, size.height * 0.12f)
            cubicTo(size.width * 0.68f, size.height * 0.18f, size.width * 0.72f, size.height * 0.36f, size.width * 0.86f, size.height * 0.34f)
            cubicTo(size.width * 1.04f, size.height * 0.32f, size.width * 1.02f, size.height * 0.84f, size.width * 0.77f, size.height * 0.9f)
            cubicTo(size.width * 0.56f, size.height * 0.96f, size.width * 0.34f, size.height * 0.96f, size.width * 0.15f, size.height * 0.82f)
            close()
        }
        drawPath(basePath, light)
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * 0.05f, size.height * 0.62f)
                cubicTo(size.width * 0.24f, size.height * 0.62f, size.width * 0.27f, size.height * 0.92f, size.width * 0.47f, size.height)
                lineTo(size.width * 0.05f, size.height)
                close()
            },
            color = primary,
        )
        drawPath(
            path = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * 0.56f, size.height * 0.88f)
                cubicTo(size.width * 0.7f, size.height * 0.66f, size.width * 0.83f, size.height * 0.62f, size.width * 0.98f, size.height * 0.62f)
                lineTo(size.width * 0.98f, size.height)
                lineTo(size.width * 0.56f, size.height)
                close()
            },
            color = primary,
        )
        if (artwork == StateArtwork.Empty) {
            val cardLeft = size.width * 0.32f
            val cardTop = size.height * 0.4f
            drawRoundRect(
                color = light,
                topLeft = Offset(cardLeft, cardTop),
                size = androidx.compose.ui.geometry.Size(size.width * 0.34f, size.height * 0.42f),
                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.dp.toPx()),
            )
            drawCircle(primary, 4.dp.toPx(), Offset(size.width * 0.44f, size.height * 0.58f))
            drawCircle(primary, 4.dp.toPx(), Offset(size.width * 0.55f, size.height * 0.58f))
            drawArc(
                color = primary,
                startAngle = 205f,
                sweepAngle = 130f,
                useCenter = false,
                topLeft = Offset(size.width * 0.43f, size.height * 0.66f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.15f, size.height * 0.12f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
            )
        } else {
            drawLine(primary, Offset(size.width * 0.22f, size.height * 0.58f), Offset(size.width * 0.5f, size.height * 0.74f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            drawLine(primary, Offset(size.width * 0.78f, size.height * 0.58f), Offset(size.width * 0.5f, size.height * 0.74f), strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            drawRoundRect(
                color = primary,
                topLeft = Offset(size.width * 0.24f, size.height * 0.52f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.18f, size.height * 0.16f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            )
            drawRoundRect(
                color = primary,
                topLeft = Offset(size.width * 0.62f, size.height * 0.52f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.18f, size.height * 0.16f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            )
            drawLine(primary, Offset(size.width * 0.47f, size.height * 0.36f), Offset(size.width * 0.52f, size.height * 0.49f), strokeWidth = 3.dp.toPx())
            drawLine(primary, Offset(size.width * 0.52f, size.height * 0.49f), Offset(size.width * 0.48f, size.height * 0.47f), strokeWidth = 3.dp.toPx())
            drawLine(primary, Offset(size.width * 0.58f, size.height * 0.38f), Offset(size.width * 0.54f, size.height * 0.49f), strokeWidth = 3.dp.toPx())
        }
    }
}