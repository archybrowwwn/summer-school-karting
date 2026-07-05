package com.apexkarting.auth.data

import com.apexkarting.auth.RequestCodeResult
import com.apexkarting.auth.VerifyCodeResult
import com.apexkarting.domain.model.Client
import com.apexkarting.domain.model.ClientId
import com.apexkarting.domain.model.Phone

fun RequestCodeResponseDto.toDomain(): RequestCodeResult = RequestCodeResult(
    ttlSeconds = ttlSeconds,
    resendAfterSeconds = resendAfterSeconds,
    devCode = code,
)

fun VerifyCodeResponseDto.toDomain(): VerifyCodeResult = VerifyCodeResult(
    token = tokens.accessToken,
    client = client.toDomain(),
    isNew = isNew,
)

fun ClientDto.toDomain(): Client = Client(
    id = ClientId(id),
    name = name,
    phone = Phone(phone),
    createdAt = createdAt,
)
