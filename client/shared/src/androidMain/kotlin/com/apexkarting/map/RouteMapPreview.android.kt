package com.apexkarting.map

import androidx.compose.runtime.Composable
import com.apexkarting.domain.model.MeetingPoint
import com.apexkarting.domain.model.Route

@Composable
actual fun RouteMapPreview(
    route: Route,
    meetingPoint: MeetingPoint,
    onOpenExternal: () -> Unit,
) {
    RouteMapPreviewFallback(route, meetingPoint, onOpenExternal)
}
