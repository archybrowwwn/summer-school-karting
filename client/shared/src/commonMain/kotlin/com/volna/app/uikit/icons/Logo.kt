package com.volna.app.uikit.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.volna.app.core.theme.ApexBrandColors

/**
 * Марка «Апекс»: клетчатый флаг (картинг) + буква A.
 * Многоцветная — tint = [androidx.compose.ui.graphics.Color.Unspecified].
 */
val Icons.Logo: ImageVector
    get() {
        if (_Logo != null) {
            return _Logo!!
        }
        val cell = 7f
        _Logo = ImageVector.Builder(
            name = "ApexMark",
            defaultWidth = 64.dp,
            defaultHeight = 64.dp,
            viewportWidth = 64f,
            viewportHeight = 64f,
        ).apply {
            for (row in 0 until 4) {
                for (col in 0 until 4) {
                    val fill = if ((row + col) % 2 == 0) {
                        ApexBrandColors.checkeredDark
                    } else {
                        ApexBrandColors.checkeredLight
                    }
                    val x0 = col * cell
                    val y0 = row * cell
                    path(fill = SolidColor(fill)) {
                        moveTo(x0, y0)
                        lineTo(x0 + cell, y0)
                        lineTo(x0 + cell, y0 + cell)
                        lineTo(x0, y0 + cell)
                        close()
                    }
                }
            }
            path(fill = SolidColor(ApexBrandColors.red)) {
                moveTo(30f, 52f)
                lineTo(38f, 20f)
                lineTo(46f, 52f)
                lineTo(42f, 52f)
                lineTo(40.5f, 44f)
                lineTo(35.5f, 44f)
                lineTo(34f, 52f)
                close()
            }
            path(fill = SolidColor(Color.White)) {
                moveTo(35.8f, 40f)
                lineTo(40.2f, 40f)
                lineTo(38.5f, 32f)
                lineTo(37.5f, 32f)
                close()
            }
        }.build()
        return _Logo!!
    }

@Suppress("ObjectPropertyName")
private var _Logo: ImageVector? = null