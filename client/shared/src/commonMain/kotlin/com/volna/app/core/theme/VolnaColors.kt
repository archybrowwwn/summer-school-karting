package com.volna.app.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Бренд картинг-центра «Апекс» (заменяет палитру учебного шаблона Volna). */
object ApexBrandColors {
    val red = Color(0xFFDC1E28)
    val redDark = Color(0xFFB01520)
    val asphalt = Color(0xFF1A1A1A)
    val checkeredLight = Color(0xFFF5F5F5)
    val checkeredDark = Color(0xFF1A1A1A)
}

@Immutable
data class VolnaColorScheme(
    val brand: Color,
    val onBrand: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
)

val VolnaLightColors = VolnaColorScheme(
    brand = ApexBrandColors.red,
    onBrand = Color.White,
    background = Color(0xFFFAFAFA),
    surface = Color.White,
    surfaceVariant = Color(0xFFF0F0F0),
    textPrimary = ApexBrandColors.asphalt,
    textSecondary = Color(0xFF6B6B6B),
    border = Color(0xFFE0E0E0),
    success = Color(0xFF237A4B),
    warning = Color(0xFF9A6400),
    error = Color(0xFFB3261E),
)