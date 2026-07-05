package com.apexkarting.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.domain.model.RouteType

private val tagShape
    @Composable get() = RoundedCornerShape(ApexTheme.tokens.radius.sm)

/** Капсула для семантических тегов: уровень и название трассы. */
private val semanticTagShape
    @Composable get() = RoundedCornerShape(percent = 50)

@Composable
internal fun ApexOutlinedTag(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    shape: Shape = tagShape,
) {
    Text(
        text = text,
        modifier = modifier
            .border(width = 1.dp, color = color, shape = shape)
            .padding(horizontal = ApexTheme.tokens.spacing.xs, vertical = ApexTheme.tokens.spacing.xxs),
        style = MaterialTheme.typography.labelMedium,
        color = color,
    )
}

@Composable
internal fun ApexFilledTag(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        modifier = modifier
            .background(color = backgroundColor, shape = tagShape)
            .padding(horizontal = ApexTheme.tokens.spacing.xs, vertical = ApexTheme.tokens.spacing.xxs),
        style = MaterialTheme.typography.labelMedium,
        color = contentColor,
        textAlign = textAlign,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TagFlowRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs),
        content = { content() },
    )
}

@Composable
internal fun NoviceTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    ApexOutlinedTag(
        text = text,
        color = ApexTheme.colors.tagNovice,
        modifier = modifier,
        shape = semanticTagShape,
    )
}

@Composable
internal fun ExperiencedTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    ApexOutlinedTag(
        text = text,
        color = ApexTheme.colors.tagExperienced,
        modifier = modifier,
        shape = semanticTagShape,
    )
}

@Composable
internal fun RouteNameTag(
    name: String,
    routeType: RouteType,
    modifier: Modifier = Modifier,
) {
    val colors = ApexTheme.colors
    ApexOutlinedTag(
        text = name,
        color = colors.routeNameTagColor(name, routeType),
        modifier = modifier,
        shape = semanticTagShape,
    )
}

@Composable
internal fun NeutralTag(
    text: String,
    modifier: Modifier = Modifier,
) {
    ApexOutlinedTag(
        text = text,
        color = ApexTheme.colors.tagNeutral,
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
        RouteType.Experienced -> ExperiencedTag(text = text, modifier = modifier)
    }
}

@Composable
internal fun BookingStatusTag(
    status: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    shape: Shape = RoundedCornerShape(ApexTheme.tokens.radius.sm),
) {
    val active = status == "Активна"
    val colors = ApexTheme.colors
    if (active) {
        Text(
            text = status,
            modifier = modifier
                .background(color = colors.statusActiveBackground, shape = shape)
                .padding(horizontal = ApexTheme.tokens.spacing.xs, vertical = ApexTheme.tokens.spacing.xxs),
            style = textStyle,
            color = colors.statusActiveText,
            textAlign = textAlign,
        )
    } else {
        Text(
            text = status,
            modifier = modifier
                .border(width = 1.dp, color = colors.tagNeutral, shape = shape)
                .padding(horizontal = ApexTheme.tokens.spacing.xs, vertical = ApexTheme.tokens.spacing.xxs),
            style = textStyle,
            color = colors.tagNeutral,
            textAlign = textAlign,
        )
    }
}