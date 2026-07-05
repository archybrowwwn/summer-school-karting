package com.apexkarting.catalog

import com.apexkarting.domain.model.Instructor
import com.apexkarting.domain.model.InstructorId
import com.apexkarting.domain.model.RouteType
import com.apexkarting.domain.model.Slot
import com.apexkarting.domain.model.SlotId
import kotlinx.datetime.Instant

data class SlotFilters(
    val dateFrom: Instant? = null,
    val dateTo: Instant? = null,
    val routeTypes: Set<RouteType> = emptySet(),
    val routeNames: Set<String> = emptySet(),
    val instructorIds: Set<InstructorId> = emptySet(),
    val onlyAvailable: Boolean = false,
)

data class PageRequest(
    val limit: Int = 20,
    val offset: Int = 0,
)

data class Page<T>(
    val items: List<T>,
    val limit: Int,
    val offset: Int,
    val total: Int,
)

interface SlotRepository {
    suspend fun listSlots(filters: SlotFilters, page: PageRequest = PageRequest()): Result<Page<Slot>>
    suspend fun getSlot(slotId: SlotId): Result<Slot>
}

interface InstructorRepository {
    suspend fun listInstructors(page: PageRequest = PageRequest(limit = 100)): Result<Page<Instructor>>
}
