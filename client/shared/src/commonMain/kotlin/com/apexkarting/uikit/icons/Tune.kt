package com.apexkarting.uikit.icons

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Tune: ImageVector
    get() {
        if (_Tune != null) return _Tune!!
        _Tune = ImageVector.Builder(
            name = "Tune",
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
                moveTo(4f, 7f)
                lineTo(20f, 7f)
                moveTo(10f, 5f)
                lineTo(10f, 9f)
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(4f, 12f)
                lineTo(20f, 12f)
                moveTo(16f, 10f)
                lineTo(16f, 14f)
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(4f, 17f)
                lineTo(20f, 17f)
                moveTo(8f, 15f)
                lineTo(8f, 19f)
            }
        }.build()
        return _Tune!!
    }

@Suppress("ObjectPropertyName")
private var _Tune: ImageVector? = null