package com.apexkarting.uikit.icons

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Profile: ImageVector
    get() {
        if (_Profile != null) return _Profile!!
        _Profile = ImageVector.Builder(
            name = "Profile",
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
                moveTo(12f, 3f)
                curveTo(7.03f, 3f, 3f, 7.03f, 3f, 12f)
                curveTo(3f, 16.97f, 7.03f, 21f, 12f, 21f)
                curveTo(16.97f, 21f, 21f, 16.97f, 21f, 12f)
                curveTo(21f, 7.03f, 16.97f, 3f, 12f, 3f)
                close()
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(12f, 11f)
                curveTo(13.38f, 11f, 14.5f, 9.88f, 14.5f, 8.5f)
                curveTo(14.5f, 7.12f, 13.38f, 6f, 12f, 6f)
                curveTo(10.62f, 6f, 9.5f, 7.12f, 9.5f, 8.5f)
                curveTo(9.5f, 9.88f, 10.62f, 11f, 12f, 11f)
                close()
            }
            path(
                stroke = ApexIconSpec.STROKE_COLOR,
                strokeLineWidth = ApexIconSpec.STROKE,
                strokeLineCap = ApexIconSpec.STROKE_CAP,
                strokeLineJoin = ApexIconSpec.STROKE_JOIN,
            ) {
                moveTo(7.5f, 17.5f)
                curveTo(8.4f, 15.9f, 10.05f, 15f, 12f, 15f)
                curveTo(13.95f, 15f, 15.6f, 15.9f, 16.5f, 17.5f)
            }
        }.build()
        return _Profile!!
    }

@Suppress("ObjectPropertyName")
private var _Profile: ImageVector? = null