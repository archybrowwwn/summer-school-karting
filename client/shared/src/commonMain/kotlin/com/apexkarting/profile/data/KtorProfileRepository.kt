package com.apexkarting.profile.data

import com.apexkarting.auth.RequestCodeResult
import com.apexkarting.auth.SessionRepository
import com.apexkarting.core.network.ApexApiClient
import com.apexkarting.domain.model.Client
import com.apexkarting.domain.model.Phone
import com.apexkarting.profile.ProfileRepository
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod

class KtorProfileRepository(
    private val apiClient: ApexApiClient,
    private val sessionRepository: SessionRepository,
) : ProfileRepository {
    override suspend fun getProfile(): Result<Client> =
        apiClient.send<ProfileClientDto>("/profile", authorized = true) {
            method = HttpMethod.Get
        }.map { it.toDomain() }

    override suspend fun updateName(name: String): Result<Client> =
        apiClient.send<ProfileClientDto>("/profile", authorized = true) {
            method = HttpMethod.Patch
            setBody(UpdateProfileRequestDto(name))
        }.map { it.toDomain() }

    override suspend fun deleteAccount(): Result<Unit> {
        val result = apiClient.sendUnit("/profile", authorized = true) {
            method = HttpMethod.Delete
        }
        if (result.isSuccess) {
            sessionRepository.clearToken()
        }
        return result
    }

    override suspend fun requestPhoneChangeCode(newPhone: Phone): Result<RequestCodeResult> =
        apiClient.send<ProfileRequestCodeResponseDto>("/profile/phone/request-code", authorized = true) {
            method = HttpMethod.Post
            setBody(ChangePhoneRequestCodeRequestDto(newPhone.value))
        }.map { it.toDomain() }

    override suspend fun confirmPhoneChange(newPhone: Phone, code: String): Result<Client> =
        apiClient.send<ProfileClientDto>("/profile/phone/confirm", authorized = true) {
            method = HttpMethod.Post
            setBody(ChangePhoneConfirmRequestDto(newPhone.value, code))
        }.map { it.toDomain() }
}
