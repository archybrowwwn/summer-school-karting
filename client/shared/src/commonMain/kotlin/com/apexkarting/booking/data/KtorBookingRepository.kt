package com.apexkarting.booking.data

import com.apexkarting.booking.BookingRepository
import com.apexkarting.booking.IdempotencyKey
import com.apexkarting.catalog.Page
import com.apexkarting.catalog.PageRequest
import com.apexkarting.core.network.ApexApiClient
import com.apexkarting.domain.model.Booking
import com.apexkarting.domain.model.BookingDraft
import com.apexkarting.domain.model.BookingId
import com.apexkarting.domain.model.BookingStatus
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod

class KtorBookingRepository(
    private val apiClient: ApexApiClient,
) : BookingRepository {
    override suspend fun createBooking(
        draft: BookingDraft,
        idempotencyKey: IdempotencyKey,
    ): Result<Booking> =
        apiClient.send<BookingDto>("/bookings", authorized = true) {
            method = HttpMethod.Post
            header("Idempotency-Key", idempotencyKey.value)
            setBody(
                CreateBookingRequestDto(
                    slotId = draft.slot.id.value,
                    seatsCount = draft.seatsCount,
                    rentalCount = draft.rentalCount,
                ),
            )
        }.map { it.toDomain() }

    override suspend fun listBookings(status: BookingStatus?, page: PageRequest): Result<Page<Booking>> =
        apiClient.send<BookingListResponseDto>("/bookings", authorized = true) {
            method = HttpMethod.Get
            status?.let { parameter("status", it.toApiValue()) }
            parameter("limit", page.limit)
            parameter("offset", page.offset)
        }.map { it.toDomain() }

    override suspend fun getBooking(bookingId: BookingId): Result<Booking> =
        apiClient.send<BookingDto>("/bookings/${bookingId.value}", authorized = true) {
            method = HttpMethod.Get
        }.map { it.toDomain() }

    override suspend fun cancelBooking(bookingId: BookingId): Result<Booking> =
        apiClient.send<BookingDto>("/bookings/${bookingId.value}/cancel", authorized = true) {
            method = HttpMethod.Post
        }.map { it.toDomain() }
}
