package com.apexkarting.booking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apexkarting.booking.BookingRepository
import com.apexkarting.booking.IdempotencyKey
import com.apexkarting.booking.IdempotencyKeyFactory
import com.apexkarting.core.error.ApiErrorCode
import com.apexkarting.core.error.AppFailure
import com.apexkarting.core.error.asAppFailure
import com.apexkarting.core.logging.AppLogger
import com.apexkarting.core.mvi.MviStore
import com.apexkarting.core.ui.ActionStatus
import com.apexkarting.domain.model.*
import com.apexkarting.domain.policy.AvailabilityPolicy
import com.apexkarting.domain.policy.AvailabilityViolation
import com.apexkarting.domain.policy.BookingPriceCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingFormState(
    val slot: Slot? = null,
    val seatsCount: Int = 1,
    val equipmentSelections: List<EquipmentSelection> = listOf(EquipmentSelection.Own),
    val actionStatus: ActionStatus = ActionStatus.Idle,
    val message: String? = null,
    val createdBooking: Booking? = null,
    val idempotencyKey: IdempotencyKey? = null,
    val idempotencyPayload: BookingPayload? = null,
) {
    val isSubmitting: Boolean = actionStatus == ActionStatus.Submitting
    val availability = slot?.let(AvailabilityPolicy::availability)
    val rentalCount: Int = equipmentSelections.count { it == EquipmentSelection.Rental }
    val totalPrice: MoneyRub? = slot?.let {
        BookingPriceCalculator.calculate(it, seatsCount, rentalCount)
    }
    val canSubmit: Boolean = slot != null &&
        !isSubmitting &&
        createdBooking == null &&
        validationMessage == null
    val validationMessage: String?
        get() {
            val currentSlot = slot ?: return null
            return AvailabilityPolicy.validate(
                BookingDraft(
                    slot = currentSlot,
                    seatsCount = seatsCount,
                    rentalCount = rentalCount,
                ),
            )?.toUserMessage()
        }
}

data class BookingPayload(
    val slotId: SlotId,
    val seatsCount: Int,
    val rentalCount: Int,
)

enum class EquipmentSelection {
    Own,
    Rental,
}

sealed interface BookingFormIntent {
    data class Open(val slot: Slot) : BookingFormIntent
    data object IncrementSeats : BookingFormIntent
    data object DecrementSeats : BookingFormIntent
    data class SetEquipmentSelection(val seatIndex: Int, val selection: EquipmentSelection) : BookingFormIntent
    data object Submit : BookingFormIntent
    data object MessageShown : BookingFormIntent
    data object SuccessDismissed : BookingFormIntent
    data object Reset : BookingFormIntent
}

sealed interface BookingFormEffect {
    data object SignedOut : BookingFormEffect
}

class BookingFormStore(
    private val bookingRepository: BookingRepository,
    private val keyFactory: IdempotencyKeyFactory,
    scope: CoroutineScope? = null,
) : ViewModel(), MviStore<BookingFormState, BookingFormIntent, BookingFormEffect> {
    private val mutableState = MutableStateFlow(BookingFormState())
    private val effects = Channel<BookingFormEffect>(Channel.BUFFERED)
    private val storeScope = scope ?: viewModelScope

    override val state: StateFlow<BookingFormState> = mutableState

    override fun accept(intent: BookingFormIntent) {
        when (intent) {
            is BookingFormIntent.Open -> open(intent.slot)
            BookingFormIntent.IncrementSeats -> changeSeats(delta = 1)
            BookingFormIntent.DecrementSeats -> changeSeats(delta = -1)
            is BookingFormIntent.SetEquipmentSelection -> setEquipmentSelection(intent.seatIndex, intent.selection)
            BookingFormIntent.Submit -> submit()
            BookingFormIntent.MessageShown -> mutableState.update { it.copy(message = null) }
            BookingFormIntent.SuccessDismissed -> mutableState.update { it.copy(createdBooking = null) }
            BookingFormIntent.Reset -> mutableState.value = BookingFormState()
        }
    }

    override suspend fun effects(): BookingFormEffect = effects.receive()

    private fun open(slot: Slot) {
        val maxSeats = AvailabilityPolicy.availability(slot).maxSeatsForBooking.coerceAtLeast(1)
        mutableState.value = BookingFormState(
            slot = slot,
            seatsCount = 1.coerceAtMost(maxSeats),
            equipmentSelections = List(1.coerceAtMost(maxSeats)) { EquipmentSelection.Own },
        )
    }

    private fun changeSeats(delta: Int) {
        mutableState.update { state ->
            val maxSeats = state.availability?.maxSeatsForBooking ?: 1
            val nextSeats = (state.seatsCount + delta).coerceIn(1, maxSeats.coerceAtLeast(1))
            val nextSelections = state.equipmentSelections
                .take(nextSeats)
                .let { current ->
                    if (current.size == nextSeats) current
                    else current + List(nextSeats - current.size) { EquipmentSelection.Own }
                }
            state.copy(
                seatsCount = nextSeats,
                equipmentSelections = enforceRentalAvailability(
                    nextSelections,
                    nextSeats,
                    state.slot?.freeRentalGear ?: 0
                ),
                idempotencyKey = null,
                idempotencyPayload = null,
                message = null,
            )
        }
    }

    private fun setEquipmentSelection(seatIndex: Int, selection: EquipmentSelection) {
        mutableState.update { state ->
            if (seatIndex !in 0 until state.seatsCount) return@update state
            val updatedSelections = state.equipmentSelections.toMutableList()
            updatedSelections[seatIndex] = selection
            state.copy(
                equipmentSelections = enforceRentalAvailability(
                    selections = updatedSelections,
                    seatsCount = state.seatsCount,
                    freeRentalGear = state.slot?.freeRentalGear ?: 0,
                ),
                idempotencyKey = null,
                idempotencyPayload = null,
                message = null,
            )
        }
    }

    private fun submit() {
        val state = mutableState.value
        val slot = state.slot ?: return
        if (state.isSubmitting) return
        state.validationMessage?.let { message ->
            mutableState.update { it.copy(message = message) }
            return
        }

        val payload = BookingPayload(
            slotId = slot.id,
            seatsCount = state.seatsCount,
            rentalCount = state.rentalCount,
        )
        val idempotencyKey = if (state.idempotencyPayload == payload) {
            state.idempotencyKey ?: keyFactory.next()
        } else {
            keyFactory.next()
        }

        storeScope.launch {
            mutableState.update {
                it.copy(
                    actionStatus = ActionStatus.Submitting,
                    message = null,
                    idempotencyKey = idempotencyKey,
                    idempotencyPayload = payload,
                )
            }
            bookingRepository.createBooking(
                draft = BookingDraft(
                    slot = slot,
                    seatsCount = payload.seatsCount,
                    rentalCount = payload.rentalCount,
                ),
                idempotencyKey = idempotencyKey,
            ).fold(
                onSuccess = { booking ->
                    mutableState.update {
                        it.copy(
                            actionStatus = ActionStatus.Idle,
                            createdBooking = booking,
                            idempotencyKey = null,
                            idempotencyPayload = null,
                        )
                    }
                },
                onFailure = { failure ->
                    AppLogger.e(failure, "Failed to create booking")
                    handleFailure(failure.asAppFailure())
                },
            )
        }
    }

    private suspend fun handleFailure(appFailure: AppFailure) {
        if (appFailure == AppFailure.Unauthorized) {
            mutableState.update { it.copy(actionStatus = ActionStatus.Idle) }
            effects.send(BookingFormEffect.SignedOut)
            return
        }

        mutableState.update { state ->
            val updatedSlot = if (appFailure is AppFailure.Api && appFailure.code == ApiErrorCode.SlotFull) {
                state.slot?.copy(
                    freeSeats = appFailure.details?.availableSeats ?: state.slot.freeSeats,
                    freeRentalGear = appFailure.details?.availableRentalGear ?: state.slot.freeRentalGear,
                )
            } else {
                state.slot
            }
            val updatedSeatsCount = updatedSlot?.let {
                state.seatsCount.coerceAtMost(
                    AvailabilityPolicy.availability(it).maxSeatsForBooking.coerceAtLeast(1),
                )
            } ?: state.seatsCount
            state.copy(
                slot = updatedSlot,
                seatsCount = updatedSeatsCount,
                equipmentSelections = enforceRentalAvailability(
                    selections = state.equipmentSelections.take(updatedSeatsCount),
                    seatsCount = updatedSeatsCount,
                    freeRentalGear = updatedSlot?.freeRentalGear ?: state.rentalCount,
                ),
                actionStatus = ActionStatus.Idle,
                message = appFailure.toUserMessage(),
            )
        }
    }
}

private fun enforceRentalAvailability(
    selections: List<EquipmentSelection>,
    seatsCount: Int,
    freeRentalGear: Int,
): List<EquipmentSelection> {
    val trimmed = selections.take(seatsCount).let { current ->
        if (current.size == seatsCount) current
        else current + List(seatsCount - current.size) { EquipmentSelection.Own }
    }
    var rentalLeft = freeRentalGear.coerceAtLeast(0)
    return trimmed.map { selection ->
        if (selection == EquipmentSelection.Rental && rentalLeft > 0) {
            rentalLeft -= 1
            EquipmentSelection.Rental
        } else {
            EquipmentSelection.Own
        }
    }
}

private fun AvailabilityViolation.toUserMessage(): String = when (this) {
    AvailabilityViolation.NoSeats -> "В этом заезде больше нет свободных картов"
    AvailabilityViolation.SlotCancelled -> "Заезд отменён"
    is AvailabilityViolation.TooManyRentalGear -> "Доступно прокатных комплектов: $freeRentalGear"
    is AvailabilityViolation.TooManySeats -> "Можно выбрать не больше $maxSeats картов"
}

private fun AppFailure.toUserMessage(): String = when (this) {
    AppFailure.NetworkUnavailable -> "Нет соединения. Проверьте интернет и попробуйте снова"
    AppFailure.Timeout -> "Сервер не ответил вовремя. Попробуйте ещё раз"
    AppFailure.Unknown -> "Не удалось оформить запись"
    AppFailure.Unauthorized -> "Сессия истекла"
    is AppFailure.Api -> message
}
