package com.apexkarting.di

import com.apexkarting.auth.AuthRepository
import com.apexkarting.auth.SessionRepository
import com.apexkarting.auth.data.DefaultSessionRepository
import com.apexkarting.auth.data.KtorAuthRepository
import com.apexkarting.auth.presentation.AuthStore
import com.apexkarting.booking.BookingRepository
import com.apexkarting.booking.IdempotencyKeyFactory
import com.apexkarting.booking.data.KtorBookingRepository
import com.apexkarting.booking.data.RandomIdempotencyKeyFactory
import com.apexkarting.booking.presentation.BookingDetailsStore
import com.apexkarting.booking.presentation.BookingFormStore
import com.apexkarting.booking.presentation.BookingListStore
import com.apexkarting.catalog.InstructorRepository
import com.apexkarting.catalog.SlotRepository
import com.apexkarting.catalog.data.KtorInstructorRepository
import com.apexkarting.catalog.data.KtorSlotRepository
import com.apexkarting.catalog.presentation.SlotDetailsStore
import com.apexkarting.catalog.presentation.SlotListStore
import com.apexkarting.core.config.AppConfig
import com.apexkarting.core.network.ApexApiClient
import com.apexkarting.core.network.platformApiBaseUrl
import com.apexkarting.core.storage.PlatformSessionStorage
import com.apexkarting.core.storage.SessionStorage
import com.apexkarting.core.time.AppClock
import com.apexkarting.core.time.SystemAppClock
import com.apexkarting.profile.ProfileRepository
import com.apexkarting.profile.data.KtorProfileRepository
import com.apexkarting.profile.presentation.ProfileStore
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.mp.KoinPlatformTools

fun initKoin() {
    if (KoinPlatformTools.defaultContext().getOrNull() != null) return
    startKoin {
        modules(apexAppModule)
    }
}

val apexAppModule = module {
    single { AppConfig() }
    single<AppClock> { SystemAppClock }
    single<SessionStorage> { PlatformSessionStorage }
    single<SessionRepository> { DefaultSessionRepository(get()) }
    single { ApexApiClient(get(), platformApiBaseUrl()) }

    single<AuthRepository> { KtorAuthRepository(get(), get()) }
    single<ProfileRepository> { KtorProfileRepository(get(), get()) }
    single<SlotRepository> { KtorSlotRepository(get()) }
    single<InstructorRepository> { KtorInstructorRepository(get()) }
    single<BookingRepository> { KtorBookingRepository(get()) }
    single<IdempotencyKeyFactory> { RandomIdempotencyKeyFactory() }

    viewModel { AuthStore(get(), get()) }
    viewModel { ProfileStore(get(), get()) }
    viewModel { SlotListStore(get(), get()) }
    viewModel { SlotDetailsStore(get()) }
    viewModel { BookingFormStore(get(), get()) }
    viewModel { BookingListStore(get(), get()) }
    viewModel { BookingDetailsStore(get(), get()) }
}
