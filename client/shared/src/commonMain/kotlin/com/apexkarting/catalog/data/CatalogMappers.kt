package com.apexkarting.catalog.data

import com.apexkarting.catalog.Page
import com.apexkarting.domain.model.GeoPoint
import com.apexkarting.domain.model.Instructor
import com.apexkarting.domain.model.InstructorId
import com.apexkarting.domain.model.MeetingPoint
import com.apexkarting.domain.model.MoneyRub
import com.apexkarting.domain.model.Route
import com.apexkarting.domain.model.RouteGeometry
import com.apexkarting.domain.model.RouteId
import com.apexkarting.domain.model.RouteType
import com.apexkarting.domain.model.Slot
import com.apexkarting.domain.model.SlotId
import com.apexkarting.domain.model.SlotStatus
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive

fun SlotListResponseDto.toDomain(): Page<Slot> = Page(
    items = items.map { it.toDomain() },
    limit = meta.limit,
    offset = meta.offset,
    total = meta.total,
)

fun InstructorListResponseDto.toDomain(): Page<Instructor> = Page(
    items = items.map { it.toDomain() },
    limit = meta.limit,
    offset = meta.offset,
    total = meta.total,
)

fun SlotDto.toDomain(): Slot = Slot(
    id = SlotId(id),
    startAt = startAt,
    route = route.toDomain(),
    instructor = instructor.toDomain(),
    totalSeats = totalSeats,
    freeSeats = freeSeats,
    freeRentalBoards = freeRentalBoards,
    price = MoneyRub(price),
    rentalPrice = MoneyRub(rentalPrice),
    meetingPoint = MeetingPoint(
        title = meetingPoint.orEmpty(),
        coordinates = GeoPoint(
            lat = meetingPointLat ?: 0.0,
            lng = meetingPointLng ?: 0.0,
        ),
    ),
    status = when (status) {
        "cancelled" -> SlotStatus.Cancelled
        else -> SlotStatus.Scheduled
    },
)

fun RouteDto.toDomain(): Route = Route(
    id = RouteId(id),
    name = name,
    type = when (type) {
        "experienced" -> RouteType.Experienced
        else -> RouteType.Novice
    },
    capacityCap = capacityCap,
    durationMin = durationMin,
    geometry = geometry?.toRouteGeometry(),
)

fun InstructorDto.toDomain(): Instructor = Instructor(
    id = InstructorId(id),
    name = name,
)

private fun JsonElement.toRouteGeometry(): RouteGeometry? {
    val points = (this as? JsonArray)?.mapNotNull { pointElement ->
        val pair = pointElement as? JsonArray ?: return@mapNotNull null
        val lat = pair.getOrNull(0)?.jsonPrimitive?.doubleOrNull
        val lng = pair.getOrNull(1)?.jsonPrimitive?.doubleOrNull
        if (lat != null && lng != null) GeoPoint(lat = lat, lng = lng) else null
    }.orEmpty()

    return if (points.isNotEmpty()) {
        RouteGeometry(points)
    } else {
        null
    }
}
