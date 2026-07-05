package com.apexkarting.map

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.apexkarting.core.theme.ApexTheme
import com.apexkarting.core.ui.ApexBottomSheet
import com.apexkarting.core.ui.ApexSheetContent
import com.apexkarting.core.ui.ApexSheetHeader
import com.apexkarting.core.ui.RouteNameTag
import com.apexkarting.core.ui.RouteTypeTag
import com.apexkarting.core.ui.TagFlowRow
import com.apexkarting.core.ui.toTagText
import com.apexkarting.domain.model.MeetingPoint
import com.apexkarting.domain.model.Route

// BS-004 / LOGIC-006: route map sheet shows a mock screenshot and hands off the meeting point to external maps.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteMapSheet(
    route: Route,
    meetingPoint: MeetingPoint,
    mapLauncher: MapLauncher = PlatformMapLauncher,
    onDismiss: () -> Unit,
) {
    ApexBottomSheet(onDismissRequest = onDismiss) {
        ApexSheetHeader(
            title = "Маршрут",
            actionText = "Закрыть",
            onActionClick = onDismiss,
        )
        ApexSheetContent {
            TagFlowRow {
                RouteTypeTag(
                    type = route.type,
                    text = route.type.toTagText(),
                )
                RouteNameTag(name = route.name, routeType = route.type)
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
                    .height(ApexTheme.tokens.sizing.buttonHeight),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text("Построить маршрут", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { mapLauncher.openExternalMap(meetingPoint) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ApexTheme.tokens.sizing.buttonHeight),
                shape = RoundedCornerShape(ApexTheme.tokens.radius.pill),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Text("Открыть в Яндекс.Картах", fontWeight = FontWeight.Bold)
            }
        }
    }
}