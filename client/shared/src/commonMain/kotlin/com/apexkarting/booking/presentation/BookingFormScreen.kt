package com.apexkarting.booking.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.core.ui.DetailScreenLayout
import com.apexkarting.core.ui.NeutralTag
import com.apexkarting.core.ui.TagFlowRow
import com.apexkarting.core.ui.contentWidthModifier
import com.apexkarting.core.ui.detailScreenContentPadding
import com.apexkarting.core.ui.RouteNameTag
import com.apexkarting.core.ui.RouteTypeTag
import com.apexkarting.core.ui.toCardStartText
import com.apexkarting.core.ui.toTagText
import com.apexkarting.domain.model.Booking
import com.apexkarting.domain.model.Slot
import com.apexkarting.domain.policy.BookingPriceCalculator
import com.apexkarting.uikit.ApexShapes
import com.apexkarting.uikit.apexClickable
import com.apexkarting.uikit.icons.Icons
import com.apexkarting.uikit.icons.Info
import com.apexkarting.uikit.icons.ApexIcon

@Composable
fun BookingFormScreen(
    slot: Slot,
    state: BookingFormState,
    onIntent: (BookingFormIntent) -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit,
    onOpenBookings: () -> Unit,
) {
    LaunchedEffect(slot.id) {
        onIntent(BookingFormIntent.Open(slot))
    }
    Box(modifier = Modifier.fillMaxSize()) {
        DetailScreenLayout(title = "Оформление записи", onBack = onBack) {
            BookingFormContent(
                state = state,
                onIntent = onIntent,
            )
        }
        state.createdBooking?.let { booking ->
            BookingSuccessSheet(
                booking = booking,
                fallbackPrice = state.totalPrice?.value ?: 0,
                onDone = onDone,
                onOpenBookings = onOpenBookings,
            )
        }
    }
}

@Composable
private fun BookingFormContent(
    state: BookingFormState,
    onIntent: (BookingFormIntent) -> Unit,
) {
    val slot = state.slot
    val maxSeats = state.availability?.maxSeatsForBooking ?: 1
    val seatPrice = slot?.price?.value ?: 0
    val rentalPrice = slot?.rentalPrice?.value ?: 0
    val seatsTotal = seatPrice * state.seatsCount
    val rentalTotal = rentalPrice * state.rentalCount
    Column(
        modifier = Modifier
            .contentWidthModifier()
            .verticalScroll(rememberScrollState())
            .padding(detailScreenContentPadding()),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
    ) {
        slot?.let {
            BookingSlotSummaryCard(it)
        }
        BookingSeatsCard(
            value = state.seatsCount,
            maxSeats = maxSeats,
            onMinus = { onIntent(BookingFormIntent.DecrementSeats) },
            onPlus = { onIntent(BookingFormIntent.IncrementSeats) },
        )
        if (slot != null) {
            BookingBoardsSection(
                equipmentSelections = state.equipmentSelections,
                freeRentalGear = slot.freeRentalGear,
                onEquipmentSelectionChange = { seatIndex, selection ->
                    onIntent(BookingFormIntent.SetEquipmentSelection(seatIndex, selection))
                },
            )
        }
        BookingPriceDetails(
            seatsCount = state.seatsCount,
            rentalCount = state.rentalCount,
            seatPrice = seatPrice,
            rentalPrice = rentalPrice,
            seatsTotal = seatsTotal,
            rentalTotal = rentalTotal,
            total = state.totalPrice?.value ?: 0,
        )
        state.validationMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        state.message?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Button(
            onClick = { onIntent(BookingFormIntent.Submit) },
            enabled = state.canSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(ApexTheme.tokens.sizing.buttonHeight),
            shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text(if (state.isSubmitting) "Записываем..." else "Записаться", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BookingSlotSummaryCard(slot: Slot) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(ApexTheme.tokens.spacing.xl),
            )
            .padding(ApexTheme.tokens.spacing.md),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
    ) {
        Text(
            text = slot.startAt.toCardStartText(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        TagFlowRow {
            RouteTypeTag(type = slot.route.type, text = slot.route.type.toTagText())
            RouteNameTag(name = slot.route.name, routeType = slot.route.type)
            NeutralTag(text = "Маршал: ${slot.instructor.name}")
        }
    }
}

@Composable
private fun BookingSeatsCard(
    value: Int,
    maxSeats: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(ApexTheme.tokens.spacing.xl),
            )
            .padding(ApexTheme.tokens.spacing.md),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
    ) {
        Text(
            text = "Число картов",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            BookingCounterButton("−", onMinus)
            Text(
                text = value.toString(),
                modifier = Modifier
                    .width(52.dp)
                    .height(54.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(ApexTheme.tokens.radius.lg))
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            BookingCounterButton("+", onPlus)
        }
        Text(
            text = "Можно записать до $maxSeats картов",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BookingCounterButton(
    text: String,
    onClick: () -> Unit,
) {
    val controlShape = ApexShapes.control()
    Text(
        text = text,
        modifier = Modifier
            .size(54.dp)
            .apexClickable(controlShape, onClick = onClick)
            .background(MaterialTheme.colorScheme.surface, controlShape)
            .border(1.dp, MaterialTheme.colorScheme.primary, controlShape)
            .padding(top = 12.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun BookingBoardsSection(
    equipmentSelections: List<EquipmentSelection>,
    freeRentalGear: Int,
    onEquipmentSelectionChange: (Int, EquipmentSelection) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm)) {
        Text(
            text = "Экипировка на каждый карт",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        equipmentSelections.forEachIndexed { index, selection ->
            val seatNumber = index + 1
            BookingBoardRow(
                label = if (seatNumber == 1) "Карт 1 (вы)" else "Карт $seatNumber (гость)",
                rentalSelected = selection == EquipmentSelection.Rental,
                rentalEnabled = selection == EquipmentSelection.Rental ||
                        equipmentSelections.count { it == EquipmentSelection.Rental } < freeRentalGear,
                onOwn = { onEquipmentSelectionChange(index, EquipmentSelection.Own) },
                onRental = { onEquipmentSelectionChange(index, EquipmentSelection.Rental) },
            )
        }
        Text(
            text = "Прокатных комплектов: ${equipmentSelections.count { it == EquipmentSelection.Rental }} из $freeRentalGear",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BookingBoardRow(
    label: String,
    rentalSelected: Boolean,
    rentalEnabled: Boolean,
    onOwn: () -> Unit,
    onRental: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(ApexTheme.tokens.spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(ApexTheme.tokens.radius.lg))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(ApexTheme.tokens.radius.lg)),
        ) {
            BoardSegment(
                text = "Своя",
                selected = !rentalSelected,
                onClick = onOwn,
            )
            BoardSegment(
                text = "Прокатная",
                selected = rentalSelected,
                enabled = rentalEnabled,
                onClick = onRental,
            )
        }
    }
}

@Composable
private fun BoardSegment(
    text: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val segmentShape = ApexShapes.control()
    Text(
        text = text,
        modifier = Modifier
            .width(100.dp)
            .height(44.dp)
            .apexClickable(segmentShape, enabled = enabled, onClick = onClick)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = segmentShape,
            )
            .padding(top = 12.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = when {
            selected -> MaterialTheme.colorScheme.onPrimary
            enabled -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
    )
}

@Composable
private fun BookingPriceDetails(
    seatsCount: Int,
    rentalCount: Int,
    seatPrice: Int,
    rentalPrice: Int,
    seatsTotal: Int,
    rentalTotal: Int,
    total: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(ApexTheme.tokens.spacing.xl),
            )
            .padding(ApexTheme.tokens.spacing.md),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
    ) {
        BookingPriceRow("Карты: $seatPrice ₽ × $seatsCount", "$seatsTotal ₽")
        BookingPriceRow("Прокат: $rentalPrice ₽ × $rentalCount", "$rentalTotal ₽")
        BookingPriceRow("Итого", "$total ₽", bold = true)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            ApexIcon(
                imageVector = Icons.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                size = 16.dp,
            )
            Text(
                text = "Оплата на месте: наличные или перевод",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BookingPriceRow(
    label: String,
    value: String,
    bold: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BookingSuccessSheet(
    booking: Booking,
    fallbackPrice: Int,
    onDone: () -> Unit,
    onOpenBookings: () -> Unit,
) {
    // CMP-15 / BS-002: successful createBooking summary; no network requests on open.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        DetailScreenLayout(title = "Вы записаны", onBack = onDone) {
            Column(
                modifier = Modifier
                    .contentWidthModifier()
                    .verticalScroll(rememberScrollState())
                    .padding(detailScreenContentPadding()),
                verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
            ) {
                BookingSuccessSummaryCard(
                    booking = booking,
                    fallbackPrice = fallbackPrice,
                )
                Button(
                    onClick = onOpenBookings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ApexTheme.tokens.sizing.buttonHeight),
                    shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text("Мои бронирования", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onDone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ApexTheme.tokens.sizing.buttonHeight),
                    shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                ) {
                    Text("Готово", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun BookingSuccessSummaryCard(
    booking: Booking,
    fallbackPrice: Int,
) {
    val slot = booking.slot
    val total = BookingPriceCalculator.calculate(booking)?.value ?: fallbackPrice
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(ApexTheme.tokens.spacing.xl),
            )
            .padding(ApexTheme.tokens.spacing.md),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.lg),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm)) {
            Text(
                text = slot?.startAt?.toCardStartText() ?: "Запись создана",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            slot?.let {
                TagFlowRow {
                    RouteTypeTag(type = it.route.type, text = it.route.type.toTagText())
                    RouteNameTag(name = it.route.name, routeType = it.route.type)
                    NeutralTag(text = "Маршал: ${it.instructor.name}")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(ApexTheme.tokens.radius.sm),
                    )
                    .padding(ApexTheme.tokens.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
            ) {
                DetailsInfoRow("Картов", booking.seatsCount.toString())
                DetailsInfoRow("Прокат экипировки", booking.rentalCount.toString())
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs)) {
            DetailsInfoRow("Итого", "$total ₽", boldValue = true)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                ApexIcon(
                    imageVector = Icons.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    size = 16.dp,
                )
                Text(
                    text = "Оплата на месте: наличные или перевод",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DetailsInfoRow(
    label: String,
    value: String,
    boldValue: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (boldValue) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
