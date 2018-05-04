package com.cutiechi.wenepu.service.impl

import com.cutiechi.wenepu.exception.AuthenticationErrorException
import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.service.AuthenticationService
import com.cutiechi.wenepu.util.JsonUtil
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class AuthenticationServiceImpl : AuthenticationService {

    override fun getAppToken(userName: String, password: String): String {
        val client = OkHttpClient()
        val body = FormBody.Builder()
                .add("method", "authUser")
                .add("xh", userName)
                .add("pwd", password)
                .build()
        val request = Request.Builder()
                .url("http://jwgl.nepu.edu.cn/app.do")
                .post(body)
                .build()
        return try {
            val response = client.newCall(request).execute()
            val statusCode = response.code()
            when (statusCode) {
                200 -> {
                    val data = response.body()?.string()
                    val token = JsonUtil.getValue(data, "token")
                    when {
                        token != "-1" -> token
                        else -> throw AuthenticationErrorException()
                    }
                }
                else -> throw ServerErrorException()
            }
        } catch (exception: Exception) {
            throw ServerErrorException()
        }
    }
}
