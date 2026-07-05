package com.apexkarting.auth.data

import com.apexkarting.auth.SessionRepository
import com.apexkarting.core.storage.SessionStorage

class DefaultSessionRepository(
    private val storage: SessionStorage,
) : SessionRepository {
    override suspend fun token(): String? = storage.readToken()

    override suspend fun saveToken(token: String) {
        storage.writeToken(token)
    }

    override suspend fun clearToken() {
        storage.clearToken()
    }
}
