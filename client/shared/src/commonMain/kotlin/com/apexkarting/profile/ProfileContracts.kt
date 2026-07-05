package com.apexkarting.profile

import com.apexkarting.auth.RequestCodeResult
import com.apexkarting.domain.model.Client
import com.apexkarting.domain.model.Phone

interface ProfileRepository {
    suspend fun getProfile(): Result<Client>
    suspend fun updateName(name: String): Result<Client>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun requestPhoneChangeCode(newPhone: Phone): Result<RequestCodeResult>
    suspend fun confirmPhoneChange(newPhone: Phone, code: String): Result<Client>
}
