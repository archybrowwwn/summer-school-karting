package com.apexkarting.core.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ApexSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
)

@Immutable
data class ApexRadius(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 14.dp,
    val lg: Dp = 16.dp,
    val pill: Dp = 32.dp,
)

@Immutable
data class ApexSizing(
    val screenMaxWidth: Dp = 393.dp,
    val contentWidth: Dp = 360.dp,
    val topTitleY: Dp = 80.dp,
    val authLogoY: Dp = 89.dp,
    val authTitleY: Dp = 190.dp,
    val authInputY: Dp = 315.dp,
    val authTermsY: Dp = 403.dp,
    val authButtonY: Dp = 464.dp,
    val listCardTopY: Dp = 136.dp,
    val listCardSecondY: Dp = 448.dp,
    val listCardHeight: Dp = 300.dp,
    val listStateMessageY: Dp = 364.dp,
    val filterIconX: Dp = 348.dp,
    val fieldHeight: Dp = 52.dp,
    val codeInputWidth: Dp = 84.dp,
    val buttonHeight: Dp = 56.dp,
    val backButtonY: Dp = 85.dp,
    val profileInfoY: Dp = 147.dp,
    val profileLinksY: Dp = 304.dp,
    val profileLogoutY: Dp = 662.dp,
    val stateMessageY: Dp = 190.dp,
    val navWidth: Dp = 300.dp,
    val navHeight: Dp = 56.dp,
    val navBottomPadding: Dp = 42.dp,
    val navContentBottomPadding: Dp = navHeight + navBottomPadding + 8.dp,
)

data class ApexTokens(
    val colors: ApexColorScheme,
    val spacing: ApexSpacing = ApexSpacing(),
    val radius: ApexRadius = ApexRadius(),
    val sizing: ApexSizing = ApexSizing(),
)

val LocalApexTokens = staticCompositionLocalOf {
    ApexTokens(colors = ApexDarkColors)
}

/**
 * Корневая тема приложения. Всегда тёмная (Grok-style), независимо от системной темы.
 */
@Composable
fun ApexTheme(
    content: @Composable () -> Unit,
) {
    val tokens = ApexTokens(colors = ApexDarkColors)
    CompositionLocalProvider(LocalApexTokens provides tokens) {
        MaterialTheme(
            colorScheme = tokens.colors.toMaterialColorScheme(),
            typography = apexTypography(tokens.colors),
            content = {
                CompositionLocalProvider(
                    LocalContentColor provides tokens.colors.textPrimary,
                ) {
                    content()
                }
            },
        )
    }
}

private fun apexTypography(colors: ApexColorScheme): Typography {
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.copy(color = colors.textPrimary),
        displayMedium = base.displayMedium.copy(color = colors.textPrimary),
        displaySmall = base.displaySmall.copy(color = colors.textPrimary),
        headlineLarge = base.headlineLarge.copy(color = colors.textPrimary),
        headlineMedium = base.headlineMedium.copy(color = colors.textPrimary),
        headlineSmall = base.headlineSmall.copy(color = colors.textPrimary),
        titleLarge = base.titleLarge.copy(color = colors.textPrimary),
        titleMedium = base.titleMedium.copy(color = colors.textPrimary),
        titleSmall = base.titleSmall.copy(color = colors.textPrimary),
        bodyLarge = base.bodyLarge.copy(color = colors.textPrimary),
        bodyMedium = base.bodyMedium.copy(color = colors.textPrimary),
        bodySmall = base.bodySmall.copy(color = colors.textSecondary),
        labelLarge = base.labelLarge.copy(color = colors.textPrimary),
        labelMedium = base.labelMedium.copy(color = colors.textPrimary),
        labelSmall = base.labelSmall.copy(color = colors.textSecondary),
    )
}

object ApexTheme {
    val tokens: ApexTokens
        @Composable get() = LocalApexTokens.current

    val colors: ApexColorScheme
        @Composable get() = tokens.colors
}

private fun ApexColorScheme.toMaterialColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = brand,
        onPrimary = onBrand,
        primaryContainer = brand.copy(alpha = 0.18f),
        onPrimaryContainer = textPrimary,
        secondary = surfaceElevated,
        onSecondary = textPrimary,
        secondaryContainer = surfaceElevated,
        onSecondaryContainer = textSecondary,
        tertiary = brandHover,
        onTertiary = onBrand,
        background = background,
        onBackground = textPrimary,
        surface = surface,
        onSurface = textPrimary,
        surfaceVariant = surfaceElevated,
        onSurfaceVariant = textSecondary,
        surfaceContainer = surface,
        surfaceContainerHigh = surfaceElevated,
        surfaceContainerHighest = surfaceElevated,
        surfaceContainerLow = background,
        outline = border,
        outlineVariant = border,
        error = error,
        onError = textPrimary,
        inverseSurface = textPrimary,
        inverseOnSurface = background,
        inversePrimary = brand,
    )
}