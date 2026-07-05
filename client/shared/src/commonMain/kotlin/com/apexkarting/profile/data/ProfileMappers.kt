package com.apexkarting.profile.data

import com.apexkarting.auth.RequestCodeResult
import com.apexkarting.domain.model.Client
import com.apexkarting.domain.model.ClientId
import com.apexkarting.domain.model.Phone

fun ProfileClientDto.toDomain(): Client = Client(
    id = ClientId(id),
    name = name,
    phone = Phone(phone),
    createdAt = createdAt,
)

fun ProfileRequestCodeResponseDto.toDomain(): RequestCodeResult = RequestCodeResult(
    ttlSeconds = ttlSeconds,
    resendAfterSeconds = resendAfterSeconds,
)
