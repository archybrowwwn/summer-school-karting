package com.apexkarting.uikit.icons

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Edit: ImageVector
    get() {
        if (_Edit != null) return _Edit!!
        _Edit = ImageVector.Builder(
            name = "Edit",
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
                moveTo(4f, 20f)
                horizontalLineTo(8.5f)
                lineTo(19.3f, 9.2f)
                curveTo(19.7f, 8.8f, 19.7f, 8.2f, 19.3f, 7.8f)
                lineTo(16.2f, 4.7f)
                curveTo(15.8f, 4.3f, 15.2f, 4.3f, 14.8f, 4.7f)
                lineTo(4f, 15.5f)
                verticalLineTo(20f)
                close()
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(13.5f, 6f)
                lineTo(18f, 10.5f)
            }
        }.build()
        return _Edit!!
    }

@Suppress("ObjectPropertyName")
private var _Edit: ImageVector? = null