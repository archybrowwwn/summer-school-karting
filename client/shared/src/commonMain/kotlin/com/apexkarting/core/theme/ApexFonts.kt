package com.apexkarting.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.apexkarting.generated.resources.Res
import com.apexkarting.generated.resources.Inter_Bold
import com.apexkarting.generated.resources.Inter_Regular
import org.jetbrains.compose.resources.Font

@Composable
fun rememberApexFontFamily(): FontFamily = FontFamily(
    Font(Res.font.Inter_Regular, FontWeight.Normal),
    Font(Res.font.Inter_Bold, FontWeight.Bold),
)

@Composable
fun apexTypography(colors: ApexColorScheme): Typography {
    val fontFamily = rememberApexFontFamily()
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.withApexFont(fontFamily, FontWeight.Bold, colors.textPrimary),
        displayMedium = base.displayMedium.withApexFont(fontFamily, FontWeight.Bold, colors.textPrimary),
        displaySmall = base.displaySmall.withApexFont(fontFamily, FontWeight.Bold, colors.textPrimary),
        headlineLarge = base.headlineLarge.withApexFont(fontFamily, FontWeight.Bold, colors.textPrimary),
        headlineMedium = base.headlineMedium.withApexFont(fontFamily, FontWeight.Bold, colors.textPrimary),
        headlineSmall = base.headlineSmall.withApexFont(fontFamily, FontWeight.Bold, colors.textPrimary),
        titleLarge = base.titleLarge.withApexFont(fontFamily, FontWeight.Bold, colors.textPrimary),
        titleMedium = base.titleMedium.withApexFont(fontFamily, FontWeight.Bold, colors.textPrimary),
        titleSmall = base.titleSmall.withApexFont(fontFamily, FontWeight.Bold, colors.textPrimary),
        bodyLarge = base.bodyLarge.withApexFont(fontFamily, FontWeight.Normal, colors.textPrimary),
        bodyMedium = base.bodyMedium.withApexFont(fontFamily, FontWeight.Normal, colors.textPrimary),
        bodySmall = base.bodySmall.withApexFont(fontFamily, FontWeight.Normal, colors.textSecondary),
        labelLarge = base.labelLarge.withApexFont(fontFamily, FontWeight.Normal, colors.textPrimary),
        labelMedium = base.labelMedium.withApexFont(fontFamily, FontWeight.Normal, colors.textPrimary),
        labelSmall = base.labelSmall.withApexFont(fontFamily, FontWeight.Normal, colors.textSecondary),
    )
}

private fun TextStyle.withApexFont(
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    color: androidx.compose.ui.graphics.Color,
): TextStyle = copy(fontFamily = fontFamily, fontWeight = fontWeight, color = color)