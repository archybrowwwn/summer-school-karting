package com.apexkarting.core.ui

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal fun Month.toMonthNameRu(): String = when (this) {
    Month.JANUARY -> "января"
    Month.FEBRUARY -> "февраля"
    Month.MARCH -> "марта"
    Month.APRIL -> "апреля"
    Month.MAY -> "мая"
    Month.JUNE -> "июня"
    Month.JULY -> "июля"
    Month.AUGUST -> "августа"
    Month.SEPTEMBER -> "сентября"
    Month.OCTOBER -> "октября"
    Month.NOVEMBER -> "ноября"
    Month.DECEMBER -> "декабря"
}

internal fun Instant.toCardStartText(): String {
    val dateTime = toLocalDateTime(TimeZone.currentSystemDefault())
    val weekday = when (dateTime.dayOfWeek) {
        DayOfWeek.MONDAY -> "Пн"
        DayOfWeek.TUESDAY -> "Вт"
        DayOfWeek.WEDNESDAY -> "Ср"
        DayOfWeek.THURSDAY -> "Чт"
        DayOfWeek.FRIDAY -> "Пт"
        DayOfWeek.SATURDAY -> "Сб"
        DayOfWeek.SUNDAY -> "Вс"
    }
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "$weekday, ${dateTime.dayOfMonth} ${dateTime.month.toMonthNameRu()} · ${dateTime.hour}:$minute"
}

internal fun Instant?.toFilterDateText(prefix: String, fallback: String): String =
    if (this == null) {
        "$prefix: $fallback"
    } else {
        val date = toLocalDateTime(TimeZone.currentSystemDefault()).date
        "$prefix: ${date.dayOfMonth} ${date.month.toMonthNameRu()}"
    }

internal fun Instant.toUiText(): String =
    toString()
        .replace("T", " ")
        .removeSuffix("Z")