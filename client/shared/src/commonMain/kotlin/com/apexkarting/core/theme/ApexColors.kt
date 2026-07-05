package com.apexkarting.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.apexkarting.domain.model.RouteType

/**
 * Централизованная дизайн-система «Апекс» — тёмная тема Nothing.
 * Единственный источник правды для цветов UI (см. [ApexTheme]).
 */
object ApexPalette {
    // Базовая палитра Nothing
    const val Background = 0xFF000000
    const val Surface = 0xFF121212
    const val SurfaceElevated = 0xFF1A1A1A
    const val TextPrimary = 0xFFFFFFFF
    const val TextSecondary = 0xFFAAAAAA
    const val Border = 0xFF333333
    const val Accent = 0xFFD71921
    const val AccentHover = 0xFFC0161D
    const val OnAccent = 0xFFFFFFFF
    const val Error = 0xFFFF4D4D

    // Бренд-лого (клетчатый флаг)
    const val CheckeredLight = 0xFFFFFFFF
    const val CheckeredDark = 0xFF333333

    // Семантика тегов (outlined: обводка = текст)
    const val TagNovice = 0xFF4CAF50
    const val TagExperienced = 0xFFFF9800
    const val TagRouteShort = 0xFF42A5F5
    const val TagRouteLong = 0xFFAB47BC
    const val TagNeutral = 0xFFAAAAAA

    // Статус бронирования «Активна» (filled)
    const val StatusActiveBackground = 0xFF4CAF50
    const val StatusActiveText = 0xFFFFFFFF

    const val Link = 0xFFD71921

    // Декоративные иллюстрации
    const val IllustrationMuted = 0xFF1A1A1A
    const val PhotoGradientStart = 0xFF121212
    const val PhotoGradientMid = 0xFF1A1A1A
    const val PhotoGradientEnd = 0xFF333333
    const val IconSecondary = 0xFFAAAAAA

    // Мок-карта
    const val MapSurface = 0xFF121212
    const val MapWater = 0xFF1A1A1A
    const val MapLand = 0xFF121212
    const val MapPark = 0xFF333333
    const val MapStreet = 0xFF333333
    const val MapRoute = 0xFFD71921
    const val MapPin = 0xFFD71921
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
    val tagNovice: Color,
    val tagExperienced: Color,
    val tagRouteShort: Color,
    val tagRouteLong: Color,
    val tagNeutral: Color,
    val statusActiveBackground: Color,
    val statusActiveText: Color,
    val link: Color,
) {
    /** Цвет outlined-тега названия конфигурации трассы. */
    fun routeNameTagColor(name: String, type: RouteType): Color = when {
        name.contains("оротк", ignoreCase = true) -> tagRouteShort
        name.contains("линн", ignoreCase = true) -> tagRouteLong
        type == RouteType.Novice -> tagRouteShort
        else -> tagRouteLong
    }
}

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
    success = Color(ApexPalette.TagNovice),
    warning = Color(ApexPalette.TagExperienced),
    error = Color(ApexPalette.Error),
    tagNovice = Color(ApexPalette.TagNovice),
    tagExperienced = Color(ApexPalette.TagExperienced),
    tagRouteShort = Color(ApexPalette.TagRouteShort),
    tagRouteLong = Color(ApexPalette.TagRouteLong),
    tagNeutral = Color(ApexPalette.TagNeutral),
    statusActiveBackground = Color(ApexPalette.StatusActiveBackground),
    statusActiveText = Color(ApexPalette.StatusActiveText),
    link = Color(ApexPalette.Link),
)

@Deprecated("Use ApexDarkColors", ReplaceWith("ApexDarkColors"))
val ApexLightColors = ApexDarkColors