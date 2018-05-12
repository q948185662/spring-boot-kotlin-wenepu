package com.cutiechi.wenepu.service.impl

import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.exception.TokenErrorException
import com.cutiechi.wenepu.service.TimeService
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class TimeServiceImpl : TimeService {

    override fun getCurrentTime(
            currentDate: String,
            appToken: String
    ): String {
        val client = OkHttpClient()
        val body = FormBody.Builder()
                .add("method", "getCurrentTime")
                .add("currDate", currentDate)
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
                        data?.length != 16 && data != null -> data.replace("zc", "weekly")
                                .replace("s_time", "weekStartTime")
                                .replace("e_time", "weekEndTime")
                                .replace("xnxqh", "semester")
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
