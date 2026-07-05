package com.apexkarting.uikit.icons

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Calendar: ImageVector
    get() {
        if (_Calendar != null) return _Calendar!!
        _Calendar = ImageVector.Builder(
            name = "Calendar",
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
                moveTo(8f, 5f)
                verticalLineTo(7f)
                moveTo(16f, 5f)
                verticalLineTo(7f)
                moveTo(5f, 9f)
                horizontalLineTo(19f)
                moveTo(5f, 9f)
                verticalLineTo(19f)
                horizontalLineTo(19f)
                verticalLineTo(9f)
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(5f, 13f)
                horizontalLineTo(19f)
            }
        }.build()
        return _Calendar!!
    }

@Suppress("ObjectPropertyName")
private var _Calendar: ImageVector? = null