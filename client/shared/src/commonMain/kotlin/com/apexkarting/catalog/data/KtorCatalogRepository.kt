package com.apexkarting.catalog.data

import com.apexkarting.catalog.InstructorRepository
import com.apexkarting.catalog.Page
import com.apexkarting.catalog.PageRequest
import com.apexkarting.catalog.SlotFilters
import com.apexkarting.catalog.SlotRepository
import com.apexkarting.domain.model.Instructor
import com.apexkarting.domain.model.RouteType
import com.apexkarting.domain.model.Slot
import com.apexkarting.domain.model.SlotId
import com.apexkarting.core.network.ApexApiClient
import io.ktor.client.request.parameter
import io.ktor.http.HttpMethod

class KtorSlotRepository(
    private val apiClient: ApexApiClient,
) : SlotRepository {
    override suspend fun listSlots(filters: SlotFilters, page: PageRequest): Result<Page<Slot>> =
        apiClient.send<SlotListResponseDto>("/slots", authorized = true) {
            method = HttpMethod.Get
            filters.dateFrom?.let { parameter("date_from", it.toString()) }
            filters.dateTo?.let { parameter("date_to", it.toString()) }
            filters.routeTypes.forEach { parameter("route_type", it.toApiValue()) }
            filters.instructorIds.forEach { parameter("instructor_id", it.value) }
            if (filters.onlyAvailable) parameter("only_available", true)
            parameter("limit", page.limit)
            parameter("offset", page.offset)
        }.map { it.toDomain() }

    override suspend fun getSlot(slotId: SlotId): Result<Slot> =
        apiClient.send<SlotDto>("/slots/${slotId.value}", authorized = true) {
            method = HttpMethod.Get
        }.map { it.toDomain() }
}

class KtorInstructorRepository(
    private val apiClient: ApexApiClient,
) : InstructorRepository {
    override suspend fun listInstructors(page: PageRequest): Result<Page<Instructor>> =
        apiClient.send<InstructorListResponseDto>("/instructors", authorized = true) {
            method = HttpMethod.Get
            parameter("limit", page.limit)
            parameter("offset", page.offset)
        }.map { it.toDomain() }
}

private fun RouteType.toApiValue(): String = when (this) {
    RouteType.Novice -> "novice"
    RouteType.Experienced -> "experienced"
}
