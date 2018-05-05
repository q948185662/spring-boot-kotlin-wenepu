package com.cutiechi.wenepu.service.impl

import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.exception.TokenErrorException
import com.cutiechi.wenepu.service.ClassroomService

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class ClassroomServiceImpl : ClassroomService {

    override fun getFreeClassroomList(
            freeDate: String,
            freeTime: String,
            teachingBuildingId: String,
            seatNumber: String,
            appToken: String
    ): String {
        val client = OkHttpClient()
        val body = FormBody.Builder()
                .add("method", "getKxJscx")
                .add("time", freeDate)
                .add("idleTime", freeTime)
                .add("xqid", "00001")
                .add("jxlid", teachingBuildingId)
                .add("classroomNumber", seatNumber)
                .build()
        val request = Request.Builder()
                .url("http://jwgl.nepu.edu.cn/app.do")
                .addHeader("token", appToken)
                .post(body)
                .build()
        return try {
            val response = client.newCall(request).execute()
            val statusCode = response.code()
            when (statusCode) {
                200 -> {
                    val data = response.body()?.string()
                    when {
                        data?.length != 16 && data != null -> data.replace("jxl", "teachingBuildingName")
                                .replace("jsList", "freeClassroomList")
                                .replace("jsid", "classroomId")
                                .replace("jsmc", "classroomName")
                                .replace("yxzws", "validSeatNumber")
                                .replace("zws", "seatNumber")
                                .replace("xqmc", "campusName")
                                .replace("jsh", "classroomNumber")
                                .replace("jzwid", "teachingBuildingId")
                                .replace("jzwmc", "teachingBuildingName")
                        else -> throw TokenErrorException()
                    }
                }
                else -> throw ServerErrorException()
            }
        } catch (exception: Exception) {
            throw ServerErrorException()
        }
    }
}
