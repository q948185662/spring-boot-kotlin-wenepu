package com.cutiechi.wenepu.service

interface TimeService {
    fun getCurrentWeek(
            currentDate: String,
            appToken: String
    ): String
}
