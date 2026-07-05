package com.apexkarting.core.time

import kotlinx.datetime.Instant
import kotlin.time.Clock

fun interface AppClock {
    fun now(): Instant
}

object SystemAppClock : AppClock {
    override fun now(): Instant = Clock.System.now()
}
