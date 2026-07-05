package com.apexkarting.catalog

import com.apexkarting.domain.model.RouteType
import com.apexkarting.domain.model.Slot

/** Известные конфигурации трасс из справочника (seed / API). */
object RouteFilterNames {
    const val SHORT_TRACK = "Короткая трасса"
    const val LONG_TRACK = "Длинная трасса"
    const val CITY_ROUTE = "Городской маршрут"
    const val SUNSET_ROUTE = "Закатный маршрут"

    val all: List<String> = listOf(SHORT_TRACK, LONG_TRACK, CITY_ROUTE, SUNSET_ROUTE)

    fun routeTypeFor(name: String): RouteType? = when (name) {
        SHORT_TRACK, CITY_ROUTE -> RouteType.Novice
        LONG_TRACK, SUNSET_ROUTE -> RouteType.Experienced
        else -> null
    }
}

/** Типы для API: сужаем выдачу с учётом AND между группами «Уровень» и «Маршрут». */
fun SlotFilters.apiRouteTypes(): Set<RouteType> {
    val typesFromNames = routeNames.mapNotNull(RouteFilterNames::routeTypeFor).toSet()
    return when {
        routeTypes.isEmpty() -> typesFromNames
        routeNames.isEmpty() -> routeTypes
        else -> routeTypes.intersect(typesFromNames)
    }
}

/** OR внутри группы, AND между «Уровень» и «Маршрут» (LOGIC-005). */
fun Slot.matchesRouteConfigFilter(filters: SlotFilters): Boolean {
    val typeMatch = filters.routeTypes.isEmpty() || route.type in filters.routeTypes
    val nameMatch = filters.routeNames.isEmpty() || route.name in filters.routeNames
    return typeMatch && nameMatch
}