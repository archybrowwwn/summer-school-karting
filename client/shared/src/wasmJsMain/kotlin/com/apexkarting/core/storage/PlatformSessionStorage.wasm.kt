package com.apexkarting.core.storage

import kotlinx.browser.localStorage

actual object PlatformSessionStorage : SessionStorage {
    actual override suspend fun readToken(): String? =
        localStorage.getItem(KEY_TOKEN)

    actual override suspend fun writeToken(token: String) {
        localStorage.setItem(KEY_TOKEN, token)
    }

    actual override suspend fun clearToken() {
        localStorage.removeItem(KEY_TOKEN)
    }

    private const val KEY_TOKEN = "apex_bearer_token"
}
