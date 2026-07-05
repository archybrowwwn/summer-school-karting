package com.apexkarting.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.domain.model.RouteType

@Composable
internal fun ApexTag(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(ApexTheme.tokens.radius.sm),
            )
            .padding(horizontal = ApexTheme.tokens.spacing.xs, vertical = ApexTheme.tokens.spacing.xxs),
        style = MaterialTheme.typography.labelMedium,
        color = contentColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
internal fun NoviceTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = ApexTheme.colors
    ApexTag(
        text = text,
        backgroundColor = colors.tagNoviceBackground,
        contentColor = colors.tagNoviceText,
        modifier = modifier,
    )
}

@Composable
internal fun RouteTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = ApexTheme.colors
    ApexTag(
        text = text,
        backgroundColor = colors.tagRouteBackground,
        contentColor = colors.tagRouteText,
        modifier = modifier,
    )
}

@Composable
internal fun NeutralTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = ApexTheme.colors
    ApexTag(
        text = text,
        backgroundColor = colors.tagNeutralBackground,
        contentColor = colors.tagNeutralText,
        modifier = modifier,
    )
}

@Composable
internal fun RouteTypeTag(
    type: RouteType,
    text: String,
    modifier: Modifier = Modifier,
) {
    when (type) {
        RouteType.Novice -> NoviceTag(text = text, modifier = modifier)
        RouteType.Experienced -> RouteTag(text = text, modifier = modifier)
    }
}