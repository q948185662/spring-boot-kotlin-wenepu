package com.cutiechi.wenepu.service

interface ClassroomService {
    fun getFreeClassroomList(
            freeDate: String,
            freeTime: String,
            teachingBuildingId: String,
            seatNumber: String,
            appToken: String
    ): String
}
