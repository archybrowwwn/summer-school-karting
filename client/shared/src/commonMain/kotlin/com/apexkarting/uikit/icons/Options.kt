package com.apexkarting.uikit.icons

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Options: ImageVector
    get() {
        if (_Options != null) return _Options!!
        _Options = ImageVector.Builder(
            name = "Options",
            defaultWidth = ApexIconSpec.SIZE,
            defaultHeight = ApexIconSpec.SIZE,
            viewportWidth = ApexIconSpec.VIEWPORT,
            viewportHeight = ApexIconSpec.VIEWPORT,
        ).apply {
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(9f, 7f)
                horizontalLineTo(19f)
                moveTo(9f, 12f)
                horizontalLineTo(19f)
                moveTo(9f, 17f)
                horizontalLineTo(19f)
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(6.5f, 7f)
                lineTo(6.501f, 7f)
                moveTo(6.5f, 12f)
                lineTo(6.501f, 12f)
                moveTo(6.5f, 17f)
                lineTo(6.501f, 17f)
            }
        }.build()
        return _Options!!
    }

@Suppress("ObjectPropertyName")
private var _Options: ImageVector? = null