package com.apexkarting.booking.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexPalette
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.uikit.ApexCard
import com.apexkarting.uikit.ApexShapes
import com.apexkarting.uikit.apexClickable
import com.apexkarting.core.ui.ListStateMessage
import com.apexkarting.core.ui.Loadable
import com.apexkarting.core.ui.BookingStatusTag
import com.apexkarting.core.ui.RouteNameTag
import com.apexkarting.core.ui.RouteTypeTag
import com.apexkarting.core.ui.ListStatePlacement
import com.apexkarting.core.ui.TabLoadingSkeletons
import com.apexkarting.core.ui.TabScreenLayout
import com.apexkarting.core.ui.TagFlowRow
import com.apexkarting.core.ui.contentWidthModifier
import com.apexkarting.core.ui.tabScreenContentPadding
import com.apexkarting.core.ui.toCardStartText
import com.apexkarting.core.ui.toTagText
import com.apexkarting.domain.model.Booking
import com.apexkarting.domain.model.BookingId
import com.apexkarting.domain.policy.BookingPriceCalculator
import kotlinx.coroutines.delay

@Composable
fun BookingListScreen(
    state: BookingListState,
    onIntent: (BookingListIntent) -> Unit,
    onBookingClick: (BookingId) -> Unit,
    onBookWalk: () -> Unit,
) {
    LaunchedEffect(Unit) {
        onIntent(BookingListIntent.Load)
    }
    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(2_500)
            onIntent(BookingListIntent.MessageShown)
        }
    }
    TabScreenLayout(title = "Мои записи") {
        when (val bookings = state.bookings) {
            Loadable.Initial,
            Loadable.Loading -> TabLoadingSkeletons()
            is Loadable.Content -> BookingGroupsContent(
                groups = bookings.value,
                refreshing = bookings.refreshing,
                message = state.message,
                onBookingClick = onBookingClick,
                onBookWalk = onBookWalk,
            )
            is Loadable.Empty -> ListStateMessage(
                title = "У вас пока нет записей",
                description = "Выберите заезд и оформите запись",
                buttonText = "Записаться",
                onClick = onBookWalk,
                artwork = null,
                placement = ListStatePlacement.TabContent,
            )
            is Loadable.Error -> ListStateMessage(
                title = "Не удалось загрузить записи",
                description = "Проверьте соединение и попробуйте снова",
                buttonText = "Обновить",
                onClick = { onIntent(BookingListIntent.Retry) },
                artwork = null,
                placement = ListStatePlacement.TabContent,
            )
        }
    }
}

@Composable
private fun BookingGroupsContent(
    groups: BookingGroups,
    refreshing: Boolean,
    message: String?,
    onBookingClick: (BookingId) -> Unit,
    onBookWalk: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(BookingListTab.Upcoming) }
    val visibleBookings = when (selectedTab) {
        BookingListTab.Upcoming -> groups.upcoming
        BookingListTab.Past -> groups.past
    }
    LazyColumn(
        modifier = Modifier
            .contentWidthModifier()
            .fillMaxHeight(),
        contentPadding = tabScreenContentPadding(),
            verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
        ) {
            if (refreshing) {
                item {
                    Text("Обновляем записи...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            message?.let {
                item {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
            item {
                BookingTabs(
                    selected = selectedTab,
                    onSelected = { selectedTab = it },
                )
            }
            if (visibleBookings.isEmpty()) {
                item {
                    BookingEmptyCard(
                        title = if (selectedTab == BookingListTab.Upcoming) {
                            "Пока нет предстоящих записей"
                        } else {
                            "Здесь появятся прошедшие заезды"
                        },
                        description = if (selectedTab == BookingListTab.Upcoming) {
                            "Можно выбрать ближайший заезд"
                        } else {
                            "Отменённые записи тоже будут здесь"
                        },
                        onBookWalk = onBookWalk,
                    )
                }
            } else {
                items(visibleBookings, key = { it.id.value }) { booking ->
                    BookingCard(
                        booking = booking,
                        pastGroup = selectedTab == BookingListTab.Past,
                        onClick = { onBookingClick(booking.id) },
                    )
                }
            }
        }
}

private enum class BookingListTab {
    Upcoming,
    Past,
}

@Composable
private fun BookingTabs(
    selected: BookingListTab,
    onSelected: (BookingListTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(ApexTheme.tokens.radius.pill))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(ApexTheme.tokens.radius.pill)),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        BookingTabButton(
            text = "Предстоящие",
            selected = selected == BookingListTab.Upcoming,
            onClick = { onSelected(BookingListTab.Upcoming) },
            modifier = Modifier.weight(1f),
        )
        BookingTabButton(
            text = "Прошедшие",
            selected = selected == BookingListTab.Past,
            onClick = { onSelected(BookingListTab.Past) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun BookingTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val chipShape = ApexShapes.chip()
    Box(
        modifier = modifier
            .fillMaxHeight()
            .apexClickable(chipShape, onClick = onClick)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                shape = chipShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BookingEmptyCard(
    title: String,
    description: String,
    onBookWalk: () -> Unit,
) {
    ApexCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs)) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedButton(
            onClick = onBookWalk,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text("Записаться")
        }
        }
    }
}

@Composable
private fun BookingCard(
    booking: Booking,
    pastGroup: Boolean,
    onClick: () -> Unit,
) {
    val slot = booking.slot
    val status = booking.statusLabel(pastGroup = pastGroup)
    ApexCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm)) {
        BookingPreviewPhoto()
        Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs)) {
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
        }
        Text(
            text = "Маршал: ${slot?.instructor?.name ?: "уточняется"}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
        BookingStatusTag(
            status = status,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .padding(top = 9.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(ApexTheme.tokens.radius.lg),
        )
        }
    }
}

@Composable
internal fun BookingPreviewPhoto() {
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
    )
}


