package com.apexkarting.domain.policy

import com.apexkarting.domain.model.BookingDraft
import com.apexkarting.domain.model.Slot
import com.apexkarting.domain.model.SlotStatus
import kotlin.math.min

data class Availability(
    val maxSeatsForBooking: Int,
    val freeRentalGear: Int,
    val canBook: Boolean,
)

sealed interface AvailabilityViolation {
    data object SlotCancelled : AvailabilityViolation
    data object NoSeats : AvailabilityViolation
    data class TooManySeats(val maxSeats: Int) : AvailabilityViolation
    data class TooManyRentalGear(val freeRentalGear: Int) : AvailabilityViolation
}

object AvailabilityPolicy {
    private const val MaxClientSeats = 5

    fun availability(slot: Slot): Availability {
        val maxSeats = if (slot.status == SlotStatus.Scheduled) {
            min(slot.freeSeats, min(slot.route.capacityCap, MaxClientSeats))
        } else {
            0
        }
        return Availability(
            maxSeatsForBooking = maxSeats,
            freeRentalGear = slot.freeRentalGear,
            canBook = maxSeats > 0,
        )
    }

    fun validate(draft: BookingDraft): AvailabilityViolation? {
        val availability = availability(draft.slot)
        return when {
            draft.slot.status != SlotStatus.Scheduled -> AvailabilityViolation.SlotCancelled
            availability.maxSeatsForBooking == 0 -> AvailabilityViolation.NoSeats
            draft.seatsCount !in 1..availability.maxSeatsForBooking ->
                AvailabilityViolation.TooManySeats(availability.maxSeatsForBooking)
            draft.rentalCount !in 0..draft.seatsCount ->
                AvailabilityViolation.TooManyRentalGear(draft.seatsCount)
            draft.rentalCount > availability.freeRentalGear ->
                AvailabilityViolation.TooManyRentalGear(availability.freeRentalGear)
            else -> null
        }
    }
}
