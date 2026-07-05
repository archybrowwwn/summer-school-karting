package com.apexkarting.auth

import com.apexkarting.core.error.AppFailure
import com.apexkarting.domain.model.Client
import com.apexkarting.domain.model.Phone

data class RequestCodeResult(
    val ttlSeconds: Int,
    val resendAfterSeconds: Int,
    val devCode: String? = null,
)

data class VerifyCodeResult(
    val token: String,
    val client: Client,
    val isNew: Boolean,
)

interface AuthRepository {
    suspend fun requestCode(phone: Phone): Result<RequestCodeResult>
    suspend fun verifyCode(phone: Phone, code: String): Result<VerifyCodeResult>
    suspend fun logout(): Result<Unit>
}

interface SessionRepository {
    suspend fun token(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}

sealed interface AuthFailure {
    data object InvalidPhone : AuthFailure
    data object InvalidCode : AuthFailure
    data object TooManyRequests : AuthFailure
    data class External(val failure: AppFailure) : AuthFailure
}
