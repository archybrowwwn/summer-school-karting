package com.apexkarting.map

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.core.ui.RouteTag
import com.apexkarting.core.ui.RouteTypeTag
import com.apexkarting.core.ui.toTagText
import com.apexkarting.domain.model.MeetingPoint
import com.apexkarting.domain.model.Route
import com.apexkarting.domain.model.RouteType

// BS-004 / LOGIC-006: route map sheet shows a mock screenshot and hands off the meeting point to external maps.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteMapSheet(
    route: Route,
    meetingPoint: MeetingPoint,
    mapLauncher: MapLauncher = PlatformMapLauncher,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(
            topStart = ApexTheme.tokens.radius.lg,
            topEnd = ApexTheme.tokens.radius.lg,
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ApexTheme.tokens.spacing.xs),
                contentAlignment = androidx.compose.ui.Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.12f)
                        .height(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                        ),
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = ApexTheme.tokens.spacing.md,
                    end = ApexTheme.tokens.spacing.md,
                    bottom = ApexTheme.tokens.spacing.md,
                ),
            verticalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Text(
                    text = "Маршрут",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Закрыть",
                    modifier = Modifier.clickable { onDismiss() },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(ApexTheme.tokens.spacing.xxs),
            ) {
                RouteTypeTag(
                    type = route.type,
                    text = route.type.toTagText(),
                )
                RouteTag(text = route.name)
            }
            RouteMapPreview(
                route = route,
                meetingPoint = meetingPoint,
                onOpenExternal = { mapLauncher.openExternalMap(meetingPoint) },
            )
            Text(
                text = "Заезд на трассе займёт ${route.durationMin} минут",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = { mapLauncher.buildRouteTo(meetingPoint) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text("Построить маршрут")
            }
            Button(
                onClick = { mapLauncher.openExternalMap(meetingPoint) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Text("Открыть в Яндекс.Картах")
            }
        }
    }
}




