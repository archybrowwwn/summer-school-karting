package com.apexkarting.map

import com.apexkarting.domain.model.MeetingPoint
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual object PlatformMapLauncher : MapLauncher {
    actual override fun openExternalMap(meetingPoint: MeetingPoint) {
        open(meetingPoint.toExternalPointUrl())
    }

    actual override fun buildRouteTo(meetingPoint: MeetingPoint) {
        open(meetingPoint.toExternalRouteUrl())
    }

    private fun open(url: String) {
        NSURL.URLWithString(url)?.let { UIApplication.sharedApplication.openURL(it) }
    }
}
