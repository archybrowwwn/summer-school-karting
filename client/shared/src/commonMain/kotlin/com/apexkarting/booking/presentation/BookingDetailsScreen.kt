package com.apexkarting.booking.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexPalette
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.core.time.AppClock
import com.apexkarting.core.ui.ApexBottomSheet
import com.apexkarting.core.ui.ApexTextLink
import com.apexkarting.core.ui.ApexFormSection
import com.apexkarting.core.ui.ApexSheetContent
import com.apexkarting.core.ui.DetailScreenLayout
import com.apexkarting.core.ui.ListStateMessage
import com.apexkarting.core.ui.ListStatePlacement
import com.apexkarting.core.ui.Loadable
import com.apexkarting.core.ui.BookingStatusTag
import com.apexkarting.core.ui.RouteNameTag
import com.apexkarting.core.ui.RouteTypeTag
import com.apexkarting.core.ui.TabLoadingSkeletons
import com.apexkarting.core.ui.TagFlowRow
import com.apexkarting.core.ui.contentWidthModifier
import com.apexkarting.core.ui.detailScreenContentPadding
import com.apexkarting.core.ui.toCardStartText
import com.apexkarting.core.ui.toTagText
import com.apexkarting.core.ui.toUiText
import com.apexkarting.domain.model.Booking
import com.apexkarting.domain.model.BookingId
import com.apexkarting.domain.policy.BookingPriceCalculator
import com.apexkarting.domain.policy.CancellationKind
import com.apexkarting.map.RouteMapSheet
import com.apexkarting.uikit.icons.Icons
import com.apexkarting.uikit.icons.Info
import com.apexkarting.uikit.icons.ApexIcon

// CMP-12 / SCR-006 / BS-003: booking details with explicit cancel confirmation.
@Composable
fun BookingDetailsScreen(
    bookingId: BookingId,
    state: BookingDetailsState,
    clock: AppClock,
    onIntent: (BookingDetailsIntent) -> Unit,
    onBack: () -> Unit,
) {
    LaunchedEffect(bookingId) {
        onIntent(BookingDetailsIntent.Load(bookingId))
    }
    Box(modifier = Modifier.fillMaxSize()) {
        DetailScreenLayout(title = "Детали записи", onBack = onBack) {
            when (val booking = state.booking) {
                Loadable.Initial,
                Loadable.Loading -> TabLoadingSkeletons()

                is Loadable.Content -> BookingDetailsContent(
                    booking = booking.value,
                    state = state,
                    clock = clock,
                    onIntent = onIntent,
                )

                is Loadable.Empty -> ListStateMessage(
                    title = "Запись недоступна",
                    description = "Вернитесь к списку и попробуйте снова",
                    buttonText = "Назад",
                    onClick = onBack,
                    artwork = null,
                    placement = ListStatePlacement.TabContent,
                )

                is Loadable.Error -> ListStateMessage(
                    title = "Не удалось загрузить запись",
                    description = "Проверьте соединение и попробуйте снова",
                    buttonText = "Обновить",
                    onClick = { onIntent(BookingDetailsIntent.Retry) },
                    artwork = null,
                    placement = ListStatePlacement.TabContent,
                )
            }
        }
        if (state.showCancelConfirm) {
            CancelConfirmSheet(
                state = state,
                clock = clock,
                onIntent = onIntent,
            )
        }
        if (state.showRouteMap) {
            state.currentBooking?.slot?.let { slot ->
                RouteMapSheet(
                    route = slot.route,
                    meetingPoint = slot.meetingPoint,
                    onDismiss = { onIntent(BookingDetailsIntent.DismissRouteMap) },
                )
            }
        }
    }
}

@Composable
private fun BookingDetailsContent(
    booking: Booking,
    state: BookingDetailsState,
    clock: AppClock,
    onIntent: (BookingDetailsIntent) -> Unit,
) {
    val slot = booking.slot
    val canCancel = state.canCancel(clock)
    Column(
        modifier = Modifier
            .contentWidthModifier()
            .verticalScroll(rememberScrollState())
            .padding(detailScreenContentPadding()),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
    ) {
        BookingDetailsEventCard(
            booking = booking,
            status = booking.statusLabel(clock),
        )
        slot?.let {
            BookingDetailsMapCard(
                address = it.meetingPoint.title.ifBlank { "уточняется" },
                onOpenMap = { onIntent(BookingDetailsIntent.OpenRouteMap) },
            )
        }
        BookingDetailsPriceBlock(booking)
        if (canCancel) {
            Text(
                text = cancelDeadlineText(booking),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        booking.cancelledAt?.let {
            Text(
                text = "Отменено: ${it.toUiText()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        state.message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        Button(
            onClick = { onIntent(BookingDetailsIntent.AskCancel) },
            enabled = canCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(ApexTheme.tokens.sizing.buttonHeight),
            shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
        ) {
            Text(if (canCancel) "Отменить" else "Отмена недоступна")
        }
    }
}

@Composable
private fun BookingDetailsEventCard(
    booking: Booking,
    status: String,
) {
    val slot = booking.slot
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
        Box {
            BookingPreviewPhoto()
            BookingStatusTag(
                status = status,
                modifier = Modifier
                    .offset(x = ApexTheme.tokens.spacing.xs, y = ApexTheme.tokens.spacing.xs)
                    .width(100.dp)
                    .height(36.dp)
                    .padding(top = 9.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(10.dp),
            )
        }
        slot?.let {
            TagFlowRow {
                RouteTypeTag(type = it.route.type, text = it.route.type.toTagText())
                RouteNameTag(name = it.route.name, routeType = it.route.type)
            }
        }
        Text(
            text = slot?.startAt?.toCardStartText() ?: "Время уточняется",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Маршал: ${slot?.instructor?.name ?: "уточняется"}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BookingDetailsMapCard(
    address: String,
    onOpenMap: () -> Unit,
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
            text = "Адрес: $address",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        BookingDetailsMapPreview()
        ApexTextLink(
            text = "Как добраться",
            onClick = onOpenMap,
        )
    }
}

@Composable
private fun BookingDetailsMapPreview() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp)
            .background(Color(ApexPalette.MapSurface), RoundedCornerShape(ApexTheme.tokens.radius.sm)),
    ) {
        val corner = 12.dp.toPx()
        drawRoundRect(Color(ApexPalette.MapWater), cornerRadius = CornerRadius(corner, corner))
        drawRoundRect(
            color = Color(ApexPalette.MapPark),
            topLeft = Offset(size.width * 0.02f, 0f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.22f, size.height),
            cornerRadius = CornerRadius(corner, corner),
        )
        drawRoundRect(
            color = Color(ApexPalette.MapPark),
            topLeft = Offset(size.width * 0.84f, 0f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.16f, size.height),
            cornerRadius = CornerRadius(corner, corner),
        )
        listOf(0.22f, 0.50f, 0.78f).forEach { y ->
            drawLine(
                color = Color(ApexPalette.MapStreet),
                start = Offset(0f, size.height * y),
                end = Offset(size.width, size.height * (y - 0.12f)),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        val routePoints = listOf(
            Offset(size.width * 0.34f, size.height * 0.88f),
            Offset(size.width * 0.48f, size.height * 0.58f),
            Offset(size.width * 0.62f, size.height * 0.36f),
        )
        routePoints.zipWithNext().forEach { (start, end) ->
            drawLine(Color(ApexPalette.MapRoute), start, end, strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
        }
        drawCircle(Color(ApexPalette.MapPin), radius = 6.dp.toPx(), center = routePoints.first())
        drawCircle(Color(ApexPalette.TextPrimary), radius = 2.5.dp.toPx(), center = routePoints.first())
    }
}

@Composable
private fun BookingDetailsPriceBlock(booking: Booking) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                text = "${booking.seatsCount} ${booking.seatsCount.pluralPlaces()} · ${booking.rentalCount} ${booking.rentalCount.pluralRentalGear()}",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${BookingPriceCalculator.calculate(booking)?.value ?: 0} ₽",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            ApexIcon(
                imageVector = Icons.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                size = com.apexkarting.uikit.icons.ApexIconSpec.SMALL_SIZE,
            )
            Text(
                text = "Оплата на месте: наличные или перевод",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CancelConfirmSheet(
    state: BookingDetailsState,
    clock: AppClock,
    onIntent: (BookingDetailsIntent) -> Unit,
) {
    val kind = state.cancellationKind(clock)
    val messageText = when (kind) {
        CancellationKind.Early -> "До старта больше 1 часа. Запись будет отменена, карты и прокатная экипировка снова станут доступны."
        CancellationKind.Late -> "До старта осталось менее 1 часа. Запись будет отменена. Штраф за позднюю отмену не взимается."
        CancellationKind.UnavailableAfterStart,
        null -> "Заезд уже начался. Отмена записи недоступна."
    }
    val cancellationLabel = when (kind) {
        CancellationKind.Early -> "Ранняя отмена"
        CancellationKind.Late -> "Поздняя отмена"
        CancellationKind.UnavailableAfterStart,
        null -> "Отмена недоступна"
    }
    val cancellationHint = when (kind) {
        CancellationKind.Early -> "Карты и прокатная экипировка освободятся."
        CancellationKind.Late -> "Ваша запись отменена. Штраф не взимается."
        CancellationKind.UnavailableAfterStart,
        null -> "Запись останется активной."
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { !state.isCancelling },
    )

    ApexBottomSheet(
        onDismissRequest = {
            if (!state.isCancelling) {
                onIntent(BookingDetailsIntent.DismissCancel)
            }
        },
        sheetState = sheetState,
    ) {
        ApexSheetContent {
            Text(
                text = "Отменить запись?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = messageText,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            ApexFormSection(title = cancellationLabel) {
                Text(
                    text = cancellationHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            state.message?.let {
                Text(
                    text = it,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Button(
                onClick = { onIntent(BookingDetailsIntent.ConfirmCancel) },
                enabled = !state.isCancelling && kind != CancellationKind.UnavailableAfterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ApexTheme.tokens.sizing.buttonHeight),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    containerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Text(if (state.isCancelling) "Отменяем..." else "Подтвердить отмену")
            }
            Button(
                onClick = { onIntent(BookingDetailsIntent.DismissCancel) },
                enabled = !state.isCancelling,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ApexTheme.tokens.sizing.buttonHeight),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text("Не отменять")
            }
        }
    }
}