package com.apexkarting.core.logging

expect object AppLogger {
    fun d(message: String)
    fun e(throwable: Throwable?, message: String)
}
