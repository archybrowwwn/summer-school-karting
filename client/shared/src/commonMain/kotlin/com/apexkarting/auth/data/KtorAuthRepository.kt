package com.apexkarting.auth.data

import com.apexkarting.auth.AuthRepository
import com.apexkarting.auth.RequestCodeResult
import com.apexkarting.auth.SessionRepository
import com.apexkarting.auth.VerifyCodeResult
import com.apexkarting.core.network.ApexApiClient
import com.apexkarting.domain.model.Phone
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod

class KtorAuthRepository(
    private val apiClient: ApexApiClient,
    private val sessionRepository: SessionRepository,
) : AuthRepository {
    override suspend fun requestCode(phone: Phone): Result<RequestCodeResult> =
        apiClient.send<RequestCodeResponseDto>("/auth/request-code") {
            postJson()
            setBody(RequestCodeRequestDto(phone.value))
        }.map { it.toDomain() }

    override suspend fun verifyCode(phone: Phone, code: String): Result<VerifyCodeResult> {
        val result = apiClient.send<VerifyCodeResponseDto>("/auth/verify-code") {
            postJson()
            setBody(VerifyCodeRequestDto(phone.value, code))
        }
        result.getOrNull()?.let { response ->
            sessionRepository.saveToken(response.tokens.accessToken)
        }
        return result.map { it.toDomain() }
    }

    override suspend fun logout(): Result<Unit> {
        val result = apiClient.sendUnit("/auth/logout", authorized = true) {
            postJson()
        }
        sessionRepository.clearToken()
        return result
    }
}

private fun HttpRequestBuilder.postJson() {
    method = HttpMethod.Post
}
