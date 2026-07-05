package com.apexkarting.domain.policy

import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

enum class CancellationKind {
    Early,
    Late,
    UnavailableAfterStart,
}

object CancellationPolicy {
    fun classify(now: Instant, slotStartAt: Instant): CancellationKind {
        if (now >= slotStartAt) {
            return CancellationKind.UnavailableAfterStart
        }
        val beforeStart = slotStartAt - now
        return if (beforeStart >= 1.hours) {
            CancellationKind.Early
        } else {
            CancellationKind.Late
        }
    }
}
