package com.apexkarting.catalog.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexPalette
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.core.ui.ApexBottomSheet
import com.apexkarting.core.ui.ApexFormSection
import com.apexkarting.core.ui.ApexSheetContent
import com.apexkarting.core.ui.ApexSheetHeader
import com.apexkarting.core.ui.ListStateMessage
import com.apexkarting.core.ui.ListStatePlacement
import com.apexkarting.core.ui.Loadable
import com.apexkarting.core.ui.StateArtwork
import com.apexkarting.core.ui.RouteNameTag
import com.apexkarting.core.ui.RouteTypeTag
import com.apexkarting.core.ui.TabLoadingSkeletons
import com.apexkarting.core.ui.TabScreenLayout
import com.apexkarting.core.ui.TagFlowRow
import com.apexkarting.core.ui.contentWidthModifier
import com.apexkarting.core.ui.tabScreenContentPadding
import com.apexkarting.core.ui.toCardStartText
import com.apexkarting.core.ui.toFilterDateText
import com.apexkarting.core.ui.toTagText
import com.apexkarting.domain.model.Instructor
import com.apexkarting.domain.model.RouteType
import com.apexkarting.domain.model.Slot
import com.apexkarting.uikit.ApexCard
import com.apexkarting.uikit.ApexShapes
import com.apexkarting.uikit.apexClickable
import com.apexkarting.uikit.icons.Icons
import com.apexkarting.uikit.icons.Tune
import com.apexkarting.uikit.icons.ApexIcon

@Composable
fun SlotListScreen(
    state: SlotListState,
    onIntent: (SlotListIntent) -> Unit,
    onSlotClick: (Slot) -> Unit,
) {
    LaunchedEffect(Unit) {
        onIntent(SlotListIntent.Load)
    }
    TabScreenLayout(
        title = "Заезды",
        trailingContent = {
            ApexIcon(
                imageVector = Icons.Tune,
                contentDescription = "Фильтры",
                modifier = Modifier.clickable { onIntent(SlotListIntent.OpenFilters) },
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                size = ApexTheme.tokens.spacing.xl,
            )
        },
    ) {
        when (val slots = state.slots) {
            Loadable.Initial -> SlotInitialLoader()
            Loadable.Loading -> TabLoadingSkeletons(cardHeight = 160.dp)
            is Loadable.Content -> SlotCards(slots.value, onSlotClick)
            is Loadable.Empty -> if (slots.reason == com.apexkarting.core.ui.EmptyReason.NoSlotsByFilters) {
                ListStateMessage(
                    title = "Нет слотов по условиям",
                    description = "Попробуйте изменить фильтры",
                    buttonText = "Фильтры",
                    artwork = StateArtwork.Empty,
                    onClick = { onIntent(SlotListIntent.OpenFilters) },
                    placement = ListStatePlacement.TabContent,
                )
            } else {
                ListStateMessage(
                    title = "Пока нет доступных заездов",
                    description = "Загляните позже",
                    placement = ListStatePlacement.TabContent,
                )
            }

            is Loadable.Error -> ListStateMessage(
                title = "Не удалось загрузить",
                description = "Проверьте соединение и попробуйте снова",
                buttonText = "Обновить",
                artwork = StateArtwork.Error,
                onClick = { onIntent(SlotListIntent.Retry) },
                placement = ListStatePlacement.TabContent,
            )
        }
    }
    if (state.filtersVisible) {
        SlotFiltersSheet(
            state = state,
            onIntent = onIntent,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SlotFiltersSheet(
    state: SlotListState,
    onIntent: (SlotListIntent) -> Unit,
) {
    ApexBottomSheet(
        onDismissRequest = { onIntent(SlotListIntent.CloseFilters) },
    ) {
        // CMP-13 / BS-001: filter form only collects conditions; SCR-002 reloads after apply.
        ApexSheetHeader(
            title = "Фильтры",
            actionText = "Сбросить",
            onActionClick = { onIntent(SlotListIntent.ResetFilters) },
        )
        ApexSheetContent(
            verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
        ) {
                FilterGroup(title = "Дата старта") {
                    FilterChipRow {
                        FilterChipButton("Сегодня", state.draftDatePreset == SlotDatePreset.Today) {
                            onIntent(SlotListIntent.SelectDatePreset(SlotDatePreset.Today))
                        }
                        FilterChipButton("Эта неделя", state.draftDatePreset == SlotDatePreset.NextSevenDays) {
                            onIntent(SlotListIntent.SelectDatePreset(SlotDatePreset.NextSevenDays))
                        }
                        FilterChipButton("Выходные", state.draftDatePreset == SlotDatePreset.Weekend) {
                            onIntent(SlotListIntent.SelectDatePreset(SlotDatePreset.Weekend))
                        }
                    }
                    DateRangePreviewRow(state)
                }

                FilterGroup(title = "Тип трассы") {
                    FilterChipRow {
                        FilterChipButton("Новичковый", RouteType.Novice in state.draftFilters.routeTypes) {
                            onIntent(SlotListIntent.ToggleRouteType(RouteType.Novice))
                        }
                        FilterChipButton("Опытный", RouteType.Experienced in state.draftFilters.routeTypes) {
                            onIntent(SlotListIntent.ToggleRouteType(RouteType.Experienced))
                        }
                    }
                }

                InstructorFilterSection(
                    instructors = state.instructors,
                    selected = state.draftFilters.instructorIds,
                    onToggle = { onIntent(SlotListIntent.ToggleInstructor(it.id)) },
                    onRetry = { onIntent(SlotListIntent.RetryInstructors) },
                )

                ApexFormSection(title = "Доступность") {
                    AvailabilitySwitchRow(
                        checked = state.draftFilters.onlyAvailable,
                        onToggle = { onIntent(SlotListIntent.ToggleOnlyAvailable) },
                    )
                }
        }
        Button(
            onClick = { onIntent(SlotListIntent.ApplyFilters) },
            modifier = Modifier
                .contentWidthModifier()
                .height(ApexTheme.tokens.sizing.buttonHeight),
            shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text("Применить", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FilterGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    ApexFormSection(title = title) {
        content()
    }
}

@Composable
private fun FilterChipRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs),
        content = { content() },
    )
}

@Composable
private fun FilterChipButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val chipShape = ApexShapes.chip()
    Text(
        text = label,
        modifier = Modifier
            .height(40.dp)
            .apexClickable(chipShape, onClick = onClick)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = chipShape,
            )
            .padding(horizontal = ApexTheme.tokens.spacing.sm, vertical = 10.dp),
        style = MaterialTheme.typography.bodyLarge,
        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun DateRangePreviewRow(state: SlotListState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs),
    ) {
        DateRangeField(
            text = state.draftFilters.dateFrom.toFilterDateText("с", "не выбрано"),
            modifier = Modifier.weight(1f),
        )
        DateRangeField(
            text = state.draftFilters.dateTo.toFilterDateText("по", "не выбрано"),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DateRangeField(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = ApexTheme.tokens.spacing.sm, vertical = 10.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun AvailabilitySwitchRow(
    checked: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = "Только со свободными картами",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
        )
    }
}

@Composable
private fun InstructorFilterSection(
    instructors: Loadable<List<Instructor>>,
    selected: Set<com.apexkarting.domain.model.InstructorId>,
    onToggle: (Instructor) -> Unit,
    onRetry: () -> Unit,
) {
    when (instructors) {
        Loadable.Initial,
        Loadable.Loading -> FilterGroup("Маршал") {
            Text("Загружаем маршалов", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        is Loadable.Empty -> Unit
        is Loadable.Error -> FilterGroup("Маршал") {
            Text("Не удалось загрузить маршалов", color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(
                onClick = onRetry,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text("Обновить")
            }
        }
        is Loadable.Content -> FilterGroup("Маршал") {
            Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs)) {
                instructors.value.chunked(2).forEach { row ->
                    FilterChipRow {
                        row.forEach { instructor ->
                            FilterChipButton(
                                label = instructor.name,
                                selected = instructor.id in selected,
                                onClick = { onToggle(instructor) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotInitialLoader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = ApexTheme.tokens.spacing.xl),
        contentAlignment = Alignment.TopCenter,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
            strokeWidth = 6.dp,
        )
    }
}

@Composable
private fun SlotCards(
    slots: List<Slot>,
    onSlotClick: (Slot) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .contentWidthModifier()
            .fillMaxHeight(),
        contentPadding = tabScreenContentPadding(),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.md),
    ) {
        items(slots, key = { it.id.value }) { slot ->
            SlotCard(slot, onSlotClick)
        }
    }
}

@Composable
private fun SlotCard(
    slot: Slot,
    onSlotClick: (Slot) -> Unit,
) {
    val canOpen = slot.freeSeats > 0
    ApexCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.listCardHeight),
        onClick = { onSlotClick(slot) },
        enabled = canOpen,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm)) {
            Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs)) {
                SlotPreviewPhoto()
                Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs)) {
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
                }
            }
            Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                text = "Маршал: ${slot.instructor.name}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
            Text(
                text = "${slot.price.value} ₽",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(ApexTheme.tokens.radius.lg),
                )
                .padding(horizontal = ApexTheme.tokens.spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                text = if (canOpen) "Свободно карт" else "Карт нет",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${slot.freeSeats} из ${slot.totalSeats}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        }
    }
}

@Composable
private fun SlotPreviewPhoto() {
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
