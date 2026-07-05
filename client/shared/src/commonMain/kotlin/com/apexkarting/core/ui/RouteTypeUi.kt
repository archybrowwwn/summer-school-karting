package com.apexkarting.core.ui

import com.apexkarting.domain.model.RouteType

internal fun RouteType.toUiText(): String = when (this) {
    RouteType.Novice -> "для новичков"
    RouteType.Experienced -> "для опытных"
}

internal fun RouteType.toTagText(): String = when (this) {
    RouteType.Novice -> "Новичковый"
    RouteType.Experienced -> "Опытный"
}

internal fun RouteType.toDetailsAudienceText(): String = when (this) {
    RouteType.Novice -> "для новичков"
    RouteType.Experienced -> "для опытных пилотов"
}