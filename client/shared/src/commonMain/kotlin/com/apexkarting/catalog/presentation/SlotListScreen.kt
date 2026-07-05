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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.core.ui.Loadable
import com.apexkarting.domain.model.Instructor
import com.apexkarting.domain.model.RouteType
import com.apexkarting.domain.model.Slot
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
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            ScreenTitle("Заезды")
            ApexIcon(
                imageVector = Icons.Tune,
                contentDescription = "Фильтры",
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.CenterEnd)
                    .padding(end = ApexTheme.tokens.sizing.screenMaxWidth - ApexTheme.tokens.sizing.filterIconX - ApexTheme.tokens.spacing.xl)
                    .clickable { onIntent(SlotListIntent.OpenFilters) },
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                size = ApexTheme.tokens.spacing.xl,
            )
        }
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when (val slots = state.slots) {
                Loadable.Initial -> SlotInitialLoader()
                Loadable.Loading -> SlotLoadingSkeleton()
                is Loadable.Content -> SlotCards(slots.value, onSlotClick)
                is Loadable.Empty -> if (slots.reason == com.apexkarting.core.ui.EmptyReason.NoSlotsByFilters) {
                    StateMessage(
                        title = "Нет слотов по условиям",
                        description = "Попробуйте изменить фильтры",
                        buttonText = "Фильтры",
                        artwork = StateArtwork.Empty,
                        onClick = { onIntent(SlotListIntent.OpenFilters) },
                    )
                } else {
                    StateMessage(
                        title = "Пока нет доступных заездов",
                        description = "Загляните позже",
                    )
                }

                is Loadable.Error -> StateMessage(
                    title = "Не удалось загрузить",
                    description = "Проверьте соединение и попробуйте снова",
                    buttonText = "Обновить",
                    artwork = StateArtwork.Error,
                    onClick = { onIntent(SlotListIntent.Retry) },
                )
            }
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
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = { onIntent(SlotListIntent.CloseFilters) },
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = ApexTheme.tokens.spacing.xl,
            topEnd = ApexTheme.tokens.spacing.xl,
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = androidx.compose.ui.Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(ApexTheme.tokens.radius.lg),
                        ),
                )
            }
        },
    ) {
        // CMP-13 / BS-001: filter form only collects conditions; SCR-002 reloads after apply.
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(androidx.compose.ui.Alignment.Center)
                        .padding(horizontal = ApexTheme.tokens.spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Text(
                        "Фильтры",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Сбросить",
                        modifier = Modifier.clickable { onIntent(SlotListIntent.ResetFilters) },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Column(
                modifier = Modifier.width(ApexTheme.tokens.sizing.contentWidth),
                verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.lg),
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

                AvailabilitySwitchRow(
                    checked = state.draftFilters.onlyAvailable,
                    onToggle = { onIntent(SlotListIntent.ToggleOnlyAvailable) },
                )
            }

            Button(
                onClick = { onIntent(SlotListIntent.ApplyFilters) },
                modifier = Modifier
                    .width(ApexTheme.tokens.sizing.contentWidth)
                    .height(ApexTheme.tokens.sizing.buttonHeight),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text("Применить", fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .width(138.dp)
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(ApexTheme.tokens.radius.pill)),
            )
            Spacer(Modifier.height(ApexTheme.tokens.spacing.xs))
        }
    }
}

@Composable
private fun FilterGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs)) {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
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
    Text(
        text = label,
        modifier = Modifier
            .height(40.dp)
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
            )
            .clickable { onClick() }
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
            .offset(y = 309.dp),
        contentAlignment = androidx.compose.ui.Alignment.TopCenter,
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
private fun SlotLoadingSkeleton() {
    SkeletonCard(y = 136.dp, height = 160.dp)
    SkeletonCard(y = 308.dp, height = 160.dp)
}

@Composable
private fun SlotCards(
    slots: List<Slot>,
    onSlotClick: (Slot) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            start = ApexTheme.tokens.spacing.md,
            end = ApexTheme.tokens.spacing.md,
            bottom = ApexTheme.tokens.sizing.navContentBottomPadding,
        ),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(ApexTheme.tokens.sizing.listCardHeight)
            .clickable(enabled = canOpen) { onSlotClick(slot) }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(ApexTheme.tokens.spacing.xl),
            )
            .padding(ApexTheme.tokens.spacing.md),
        verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xs)) {
            SlotPreviewPhoto()
            Column(verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs)) {
                Row(horizontalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs)) {
                    RouteTypeSlotTag(type = slot.route.type, text = slot.route.type.toTagText())
                    RouteSlotTag(
                        text = slot.route.name,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                }
                Text(
                    text = slot.startAt.toSlotCardStartText(),
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
                modifier = Modifier.weight(1f),
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

@Composable
private fun SlotPreviewPhoto() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFD8EEF0),
                        Color(0xFFF7F0D8),
                        Color(0xFFCFE4E8),
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
                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.36f)),
                    ),
                    shape = RoundedCornerShape(ApexTheme.tokens.radius.lg),
                ),
        )
    }
}
