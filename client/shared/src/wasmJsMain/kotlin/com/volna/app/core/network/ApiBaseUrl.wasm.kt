package com.volna.app.core.network

import kotlinx.browser.window

internal actual fun platformApiBaseUrl(): String =
    "http://${window.location.hostname}:8080"