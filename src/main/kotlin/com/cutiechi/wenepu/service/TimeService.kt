package com.cutiechi.wenepu.service

interface TimeService {
    fun getCurrentTime(
            currentDate: String,
            appToken: String
    ): String
}
