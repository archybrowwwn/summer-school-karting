package com.apexkarting.core.network

import kotlinx.browser.window

internal actual fun platformApiBaseUrl(): String {
    val apiHost = when (val host = window.location.hostname) {
        "[::1]", "::1" -> "127.0.0.1"
        else -> host
    }
    return "http://$apiHost:8080"
}