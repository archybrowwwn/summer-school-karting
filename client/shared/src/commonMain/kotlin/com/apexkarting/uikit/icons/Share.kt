package com.apexkarting.uikit.icons

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Share: ImageVector
    get() {
        if (_Share != null) return _Share!!
        _Share = ImageVector.Builder(
            name = "Share",
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
                moveTo(16f, 8f)
                curveTo(17.1f, 8f, 18f, 7.1f, 18f, 6f)
                curveTo(18f, 4.9f, 17.1f, 4f, 16f, 4f)
                curveTo(14.9f, 4f, 14f, 4.9f, 14f, 6f)
                curveTo(14f, 7.1f, 14.9f, 8f, 16f, 8f)
                close()
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(8f, 14f)
                curveTo(9.1f, 14f, 10f, 13.1f, 10f, 12f)
                curveTo(10f, 10.9f, 9.1f, 10f, 8f, 10f)
                curveTo(6.9f, 10f, 6f, 10.9f, 6f, 12f)
                curveTo(6f, 13.1f, 6.9f, 14f, 8f, 14f)
                close()
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(16f, 20f)
                curveTo(17.1f, 20f, 18f, 19.1f, 18f, 18f)
                curveTo(18f, 16.9f, 17.1f, 16f, 16f, 16f)
                curveTo(14.9f, 16f, 14f, 16.9f, 14f, 18f)
                curveTo(14f, 19.1f, 14.9f, 20f, 16f, 20f)
                close()
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(9.5f, 13f)
                lineTo(14.5f, 7f)
                moveTo(9.5f, 11f)
                lineTo(14.5f, 17f)
            }
        }.build()
        return _Share!!
    }

@Suppress("ObjectPropertyName")
private var _Share: ImageVector? = null