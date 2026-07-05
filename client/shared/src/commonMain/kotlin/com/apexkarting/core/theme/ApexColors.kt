package com.apexkarting.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Семантическая палитра «Апекс» — тёмная тема в стиле Grok.
 * Единственный источник правды для цветов UI (см. [ApexTheme]).
 */
object ApexPalette {
    const val Background = 0xFF0B0B0F
    const val Surface = 0xFF16161A
    const val SurfaceElevated = 0xFF1F1F24
    const val TextPrimary = 0xFFF4F4F5
    const val TextSecondary = 0xFFA1A1AA
    const val Border = 0xFF27272A
    const val Accent = 0xFFF97316
    const val AccentHover = 0xFFEA580C
    const val OnAccent = 0xFF0B0B0F

    // Бренд-лого (клетчатый флаг)
    const val CheckeredLight = 0xFFF4F4F5
    const val CheckeredDark = 0xFF27272A

    // Теги и статусы (тёмная тема: приглушённый фон + светлый текст)
    const val TagNoviceBackground = 0xFF1A3D28
    const val TagNoviceText = 0xFF86EFAC
    const val TagRouteBackground = 0xFF3D3318
    const val TagRouteText = 0xFFFCD34D
    const val TagNeutralBackground = 0xFF27272A
    const val TagNeutralText = 0xFFF4F4F5
    const val StatusActiveBackground = 0xFF1A3D28
    const val StatusActiveText = 0xFF4ADE80
    const val Link = 0xFFFB923C
}

@Immutable
data class ApexColorScheme(
    val brand: Color,
    val brandHover: Color,
    val onBrand: Color,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val tagNoviceBackground: Color,
    val tagNoviceText: Color,
    val tagRouteBackground: Color,
    val tagRouteText: Color,
    val tagNeutralBackground: Color,
    val tagNeutralText: Color,
    val statusActiveBackground: Color,
    val statusActiveText: Color,
    val link: Color,
)

/** Глобальная тёмная тема приложения. */
val ApexDarkColors = ApexColorScheme(
    brand = Color(ApexPalette.Accent),
    brandHover = Color(ApexPalette.AccentHover),
    onBrand = Color(ApexPalette.OnAccent),
    background = Color(ApexPalette.Background),
    surface = Color(ApexPalette.Surface),
    surfaceElevated = Color(ApexPalette.SurfaceElevated),
    textPrimary = Color(ApexPalette.TextPrimary),
    textSecondary = Color(ApexPalette.TextSecondary),
    border = Color(ApexPalette.Border),
    success = Color(0xFF4ADE80),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFF87171),
    tagNoviceBackground = Color(ApexPalette.TagNoviceBackground),
    tagNoviceText = Color(ApexPalette.TagNoviceText),
    tagRouteBackground = Color(ApexPalette.TagRouteBackground),
    tagRouteText = Color(ApexPalette.TagRouteText),
    tagNeutralBackground = Color(ApexPalette.TagNeutralBackground),
    tagNeutralText = Color(ApexPalette.TagNeutralText),
    statusActiveBackground = Color(ApexPalette.StatusActiveBackground),
    statusActiveText = Color(ApexPalette.StatusActiveText),
    link = Color(ApexPalette.Link),
)

@Deprecated("Use ApexDarkColors", ReplaceWith("ApexDarkColors"))
val ApexLightColors = ApexDarkColors