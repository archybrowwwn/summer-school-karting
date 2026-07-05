package com.apexkarting.booking.presentation

import com.apexkarting.core.time.AppClock
import com.apexkarting.core.ui.toUiText
import com.apexkarting.domain.model.Booking
import com.apexkarting.domain.model.BookingStatus
import kotlin.time.Duration.Companion.hours

internal fun Booking.statusLabel(clock: AppClock? = null, pastGroup: Boolean = false): String = when {
    status == BookingStatus.Cancelled -> "Отменена"
    status == BookingStatus.LateCancel -> "Поздняя отмена"
    pastGroup -> "Прошедшая"
    clock != null && slot?.startAt?.let { it <= clock.now() } == true -> "Прошедшая"
    else -> "Активна"
}

internal fun cancelDeadlineText(booking: Booking): String =
    "Бесплатно освободить карт можно до ${booking.slot?.startAt?.minus(1.hours)?.toUiText() ?: "уточняется"}"

internal fun Int.pluralPlaces(): String = when {
    this % 10 == 1 && this % 100 != 11 -> "карт"
    this % 10 in 2..4 && this % 100 !in 12..14 -> "карта"
    else -> "картов"
}

internal fun Int.pluralRentalBoards(): String = when {
    this % 10 == 1 && this % 100 != 11 -> "прокатный комплект"
    this % 10 in 2..4 && this % 100 !in 12..14 -> "прокатных комплекта"
    else -> "прокатных комплектов"
}