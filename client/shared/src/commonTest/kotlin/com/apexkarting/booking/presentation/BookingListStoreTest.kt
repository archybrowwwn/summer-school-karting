package com.apexkarting.booking.presentation

import com.apexkarting.booking.BookingRepository
import com.apexkarting.booking.IdempotencyKey
import com.apexkarting.catalog.Page
import com.apexkarting.catalog.PageRequest
import com.apexkarting.core.error.AppFailure
import com.apexkarting.core.error.AppFailureException
import com.apexkarting.core.time.AppClock
import com.apexkarting.core.ui.Loadable
import com.apexkarting.domain.model.Booking
import com.apexkarting.domain.model.BookingDraft
import com.apexkarting.domain.model.BookingId
import com.apexkarting.domain.model.BookingStatus
import com.apexkarting.domain.model.GeoPoint
import com.apexkarting.domain.model.Instructor
import com.apexkarting.domain.model.InstructorId
import com.apexkarting.domain.model.MeetingPoint
import com.apexkarting.domain.model.MoneyRub
import com.apexkarting.domain.model.Route
import com.apexkarting.domain.model.RouteId
import com.apexkarting.domain.model.RouteType
import com.apexkarting.domain.model.Slot
import com.apexkarting.domain.model.SlotId
import com.apexkarting.domain.model.SlotStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

class BookingListStoreTest {
    @Test
    fun refreshFailureKeepsExistingBookingsAndShowsMessage() = runTest {
        val booking = booking(slotStartAt = Instant.parse("2026-07-01T12:00:00Z"))
        val repository = FakeBookingRepository(
            listResults = listOf(
                Result.success(Page(items = listOf(booking), limit = 100, offset = 0, total = 1)),
                Result.failure(AppFailureException(AppFailure.NetworkUnavailable)),
            ),
        )
        val store = BookingListStore(
            bookingRepository = repository,
            clock = AppClock { Instant.parse("2026-06-01T10:00:00Z") },
            scope = CoroutineScope(coroutineContext),
        )

        store.accept(BookingListIntent.Load)
        yield()
        store.accept(BookingListIntent.Refresh)
        yield()

        val state = store.state.value
        val content = assertIs<Loadable.Content<BookingGroups>>(state.bookings)
        assertEquals(listOf(booking), content.value.upcoming)
        assertEquals(emptyList(), content.value.past)
        assertFalse(content.refreshing)
        assertEquals("Не удалось обновить. Проверьте соединение и попробуйте снова.", state.message)
        assertEquals(2, repository.listCalls)
    }

    private class FakeBookingRepository(
        private val listResults: List<Result<Page<Booking>>>,
    ) : BookingRepository {
        var listCalls: Int = 0
            private set

        override suspend fun createBooking(draft: BookingDraft, idempotencyKey: IdempotencyKey): Result<Booking> =
            Result.failure(UnsupportedOperationException())

        override suspend fun listBookings(status: BookingStatus?, page: PageRequest): Result<Page<Booking>> {
            val result = listResults[listCalls]
            listCalls += 1
            return result
        }

        override suspend fun getBooking(bookingId: BookingId): Result<Booking> =
            Result.failure(UnsupportedOperationException())

        override suspend fun cancelBooking(bookingId: BookingId): Result<Booking> =
            Result.failure(UnsupportedOperationException())
    }

    private fun booking(slotStartAt: Instant): Booking = Booking(
        id = BookingId("booking-1"),
        slotId = SlotId("slot-1"),
        clientId = null,
        seatsCount = 1,
        rentalCount = 0,
        status = BookingStatus.Active,
        priceTotal = null,
        createdAt = Instant.parse("2026-06-01T09:00:00Z"),
        cancelledAt = null,
        slot = Slot(
            id = SlotId("slot-1"),
            startAt = slotStartAt,
            route = Route(
                id = RouteId("route-1"),
                name = "Короткая трасса",
                type = RouteType.Novice,
                capacityCap = 8,
                durationMin = 90,
            ),
            instructor = Instructor(InstructorId("instructor-1"), "Мария"),
            totalSeats = 8,
            freeSeats = 4,
            freeRentalGear = 4,
            price = MoneyRub(2_500),
            rentalPrice = MoneyRub(800),
            meetingPoint = MeetingPoint("Павильон у стартовой прямой", GeoPoint(59.978, 30.262)),
            status = SlotStatus.Scheduled,
        ),
        isFirstBooking = null,
    )
}
