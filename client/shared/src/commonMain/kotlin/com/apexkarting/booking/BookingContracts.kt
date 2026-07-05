package com.apexkarting.booking

import com.apexkarting.catalog.Page
import com.apexkarting.catalog.PageRequest
import com.apexkarting.domain.model.Booking
import com.apexkarting.domain.model.BookingDraft
import com.apexkarting.domain.model.BookingId
import com.apexkarting.domain.model.BookingStatus
import kotlin.jvm.JvmInline

@JvmInline
value class IdempotencyKey(val value: String)

interface IdempotencyKeyFactory {
    fun next(): IdempotencyKey
}

interface BookingRepository {
    suspend fun createBooking(draft: BookingDraft, idempotencyKey: IdempotencyKey): Result<Booking>
    suspend fun listBookings(status: BookingStatus? = null, page: PageRequest = PageRequest()): Result<Page<Booking>>
    suspend fun getBooking(bookingId: BookingId): Result<Booking>
    suspend fun cancelBooking(bookingId: BookingId): Result<Booking>
}
