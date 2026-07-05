package com.apexkarting.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.apexkarting.core.theme.ApexTheme

@Composable
internal fun TabScreenLayout(
    title: String,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TabScreenHeader(
            title = title,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter,
            content = content,
        )
    }
}

@Composable
internal fun TabScreenHeader(
    title: String,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val headerInset = ApexTheme.tokens.sizing.headerInset
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.tabHeaderHeight)
            .padding(horizontal = headerInset),
        contentAlignment = Alignment.Center,
    ) {
        if (leadingContent != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart),
                contentAlignment = Alignment.Center,
            ) {
                leadingContent()
            }
        }
        Text(
            text = title,
            modifier = Modifier.padding(
                horizontal = ApexTheme.tokens.sizing.headerIconTouchSize + headerInset,
            ),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        if (trailingContent != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterEnd),
                contentAlignment = Alignment.Center,
            ) {
                trailingContent()
            }
        }
    }
}

@Composable
internal fun DetailScreenLayout(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    TabScreenLayout(
        title = title,
        modifier = modifier,
        leadingContent = {
            ApexBackButton(
                onClick = onBack,
                style = BackButtonStyle.Floating,
            )
        },
        trailingContent = trailingContent,
        content = content,
    )
}

@Composable
internal fun Modifier.contentWidthModifier(): Modifier {
    return this.width(ApexTheme.tokens.sizing.contentWidth)
}

@Composable
internal fun tabScreenContentPadding(): PaddingValues {
    return PaddingValues(
        top = ApexTheme.tokens.spacing.md,
        bottom = ApexTheme.tokens.sizing.navContentBottomPadding + ApexTheme.tokens.spacing.lg,
    )
}

@Composable
internal fun detailScreenContentPadding(): PaddingValues {
    return PaddingValues(
        top = ApexTheme.tokens.spacing.md,
        bottom = ApexTheme.tokens.spacing.lg,
    )
}

@Composable
internal fun TabLoadingSkeletons(
    modifier: Modifier = Modifier,
    cardHeight: Dp = ApexTheme.tokens.sizing.listCardHeight,
) {
    Column(
        modifier = modifier
            .contentWidthModifier()
            .padding(top = ApexTheme.tokens.spacing.md),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
    ) {
        ListSkeletonCard(height = cardHeight)
        ListSkeletonCard(height = cardHeight)
    }
}