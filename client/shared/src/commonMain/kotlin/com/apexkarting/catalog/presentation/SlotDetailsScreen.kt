package com.apexkarting.catalog.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexPalette
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.core.ui.ApexTextLink
import com.apexkarting.core.ui.DetailScreenLayout

import com.apexkarting.core.ui.ListStateMessage
import com.apexkarting.core.ui.ListStatePlacement
import com.apexkarting.core.ui.Loadable
import com.apexkarting.core.ui.RouteNameTag
import com.apexkarting.core.ui.RouteTypeTag
import com.apexkarting.core.ui.TabLoadingSkeletons
import com.apexkarting.core.ui.TagFlowRow
import com.apexkarting.core.ui.contentWidthModifier
import com.apexkarting.core.ui.detailScreenContentPadding
import com.apexkarting.core.ui.toCardStartText
import com.apexkarting.core.ui.toDetailsAudienceText
import com.apexkarting.core.ui.toTagText
import com.apexkarting.domain.model.Slot
import com.apexkarting.domain.model.SlotId
import com.apexkarting.domain.policy.AvailabilityPolicy
import com.apexkarting.map.RouteMapSheet


@Composable
fun SlotDetailsScreen(
    slotId: SlotId,
    state: SlotDetailsState,
    onIntent: (SlotDetailsIntent) -> Unit,
    onBack: () -> Unit,
    onBook: (Slot) -> Unit,
) {
    LaunchedEffect(slotId) {
        onIntent(SlotDetailsIntent.Load(slotId))
    }
    Box(Modifier.fillMaxSize()) {
        DetailScreenLayout(
            title = "Заезд",
            onBack = onBack,
        ) {
            when (val slot = state.slot) {
                Loadable.Initial,
                Loadable.Loading -> TabLoadingSkeletons()
                is Loadable.Content -> SlotDetailsContent(
                    slot = slot.value,
                    onBook = { onBook(slot.value) },
                    onOpenMap = { onIntent(SlotDetailsIntent.OpenRouteMap) },
                )
                is Loadable.Empty -> ListStateMessage(
                    title = "Заезд недоступен",
                    description = "Попробуйте выбрать другой слот",
                    buttonText = "Назад",
                    onClick = onBack,
                    placement = ListStatePlacement.TabContent,
                )
                is Loadable.Error -> ListStateMessage(
                    title = "Не удалось загрузить",
                    description = "Проверьте соединение и попробуйте снова",
                    buttonText = "Повторить",
                    onClick = { onIntent(SlotDetailsIntent.Retry) },
                    placement = ListStatePlacement.TabContent,
                )
            }
        }
        if (state.showRouteMap) {
            (state.slot as? Loadable.Content)?.value?.let { slot ->
                RouteMapSheet(
                    route = slot.route,
                    meetingPoint = slot.meetingPoint,
                    onDismiss = { onIntent(SlotDetailsIntent.DismissRouteMap) },
                )
            }
        }
    }
}

@Composable
private fun SlotDetailsContent(
    slot: Slot,
    onBook: () -> Unit,
    onOpenMap: () -> Unit,
) {
    val availability = AvailabilityPolicy.availability(slot)
    Column(
        modifier = Modifier
            .contentWidthModifier()
            .verticalScroll(rememberScrollState())
            .padding(detailScreenContentPadding()),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
    ) {
        SlotDetailsEventCard(slot = slot)
        SlotDetailsMapCard(slot = slot, onOpenMap = onOpenMap)
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
            DetailsInfoRow("Свободно карт", "${slot.freeSeats} из ${slot.totalSeats}")
            DetailsInfoRow(
                "Прокатная экипировка (доступно ${availability.freeRentalGear} шт.)",
                "${slot.rentalPrice.value} ₽",
                boldValue = true,
            )
            DetailsInfoRow("Цена", "${slot.price.value} ₽", boldValue = true)
        }
        Text(
            text = "Оплата на месте: наличные или перевод",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(
            onClick = onBook,
            enabled = availability.canBook,
            modifier = Modifier
                .fillMaxWidth()
                .height(ApexTheme.tokens.sizing.buttonHeight),
            shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            Text(if (availability.canBook) "Записаться" else "Карт нет", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SlotDetailsEventCard(slot: Slot) {
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
        SlotDetailsPreviewPhoto()
        TagFlowRow {
            RouteTypeTag(type = slot.route.type, text = slot.route.type.toTagText())
            RouteNameTag(name = slot.route.name, routeType = slot.route.type)
        }
        Text(
            text = slot.startAt.toCardStartText(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Заезд на трассе «${slot.route.name}» займёт ${slot.route.durationMin} минут и подойдёт ${slot.route.type.toDetailsAudienceText()}.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Маршал: ${slot.instructor.name}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SlotDetailsPreviewPhoto() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(ApexPalette.PhotoGradientStart),
                        Color(ApexPalette.PhotoGradientMid),
                        Color(ApexPalette.PhotoGradientEnd),
                    ),
                ),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.lg),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(ApexPalette.Background).copy(alpha = 0.5f)),
                    ),
                    shape = RoundedCornerShape(ApexTheme.tokens.radius.lg),
                ),
        )
    }
}

@Composable
private fun SlotDetailsMapCard(
    slot: Slot,
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
            text = "Адрес: ${slot.meetingPoint.title.ifBlank { "уточняется" }}",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
        SlotDetailsMapPreview()
        ApexTextLink(
            text = "Как добраться",
            onClick = onOpenMap,
        )
    }
}

@Composable
private fun SlotDetailsMapPreview() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp)
            .background(Color(ApexPalette.MapSurface), RoundedCornerShape(ApexTheme.tokens.radius.sm)),
    ) {
        val corner = 12.dp.toPx()
        drawRoundRect(
            color = Color(ApexPalette.MapWater),
            cornerRadius = CornerRadius(corner, corner),
        )
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
            drawLine(
                color = Color(ApexPalette.MapRoute),
                start = start,
                end = end,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        drawCircle(Color(ApexPalette.MapPin), radius = 6.dp.toPx(), center = routePoints.first())
        drawCircle(Color(ApexPalette.TextPrimary), radius = 2.5.dp.toPx(), center = routePoints.first())
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
