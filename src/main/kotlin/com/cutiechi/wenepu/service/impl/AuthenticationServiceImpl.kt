package com.cutiechi.wenepu.service.impl

import com.cutiechi.wenepu.exception.AuthenticationErrorException
import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.service.AuthenticationService
import com.cutiechi.wenepu.util.JsonUtil
import com.cutiechi.wenepu.util.VerificationCodeImageRecognitionUtil

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.lang.Exception
import javax.imageio.ImageIO

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

    private fun getWebVerification(): Array<String> {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url("http://jwgl.nepu.edu.cn/verifycode.servlet")
                .build()
        var webToken = ""
        var webVerificationCode = ""
        do {
            try {
                val response = client.newCall(request).execute()
                val statusCode = response.code()
                if (statusCode == 200) {
                    val verificationCodeImage = ImageIO.read(response.body()!!.byteStream())
                    webVerificationCode = VerificationCodeImageRecognitionUtil.recognition(verificationCodeImage)
                    if (!webVerificationCode.contains("m")) {
                        webToken = response.header("Set-Cookie")!!.substring(11, 43)
                        break
                    } else {
                        continue
                    }
                } else {
                    break
                }
            } catch (exception: Exception) {
                break
            }
        } while (true)
        return arrayOf(webToken, webVerificationCode)
    }

    override fun getWebToken(userName: String, password: String): String {
        val webVerification = this.getWebVerification()
        val webToken = webVerification[0]
        val webVerificationCode = webVerification[1]
        return when {
            webToken != "" && webVerificationCode != "" -> {
                val connection = Jsoup.connect("http://jwgl.nepu.edu.cn/Logon.do")
                        .data("method", "logon")
                        .data("USERNAME", userName)
                        .data("PASSWORD", password)
                        .data("RANDOMCODE", webVerificationCode)
                        .cookie("JSESSIONID", webToken)
                        .timeout(6000)
                try {
                    val document = connection.post()
                    val title = document.title()
                    when {
                        title.length != 21 -> {
                            Jsoup.connect("http://jwgl.nepu.edu.cn/framework/main.jsp")
                                    .cookie("JSESSIONID", webToken)
                                    .timeout(6000)
                                    .post()
                            Jsoup.connect("http://jwgl.nepu.edu.cn/Logon.do?method=logonBySSO")
                                    .cookie("JSESSIONID", webToken)
                                    .timeout(6000)
                                    .post()
                            webToken
                        }
                        else -> throw AuthenticationErrorException()
                    }
                } catch (exception: Exception) {
                    throw ServerErrorException()
                }
            }
            else -> throw ServerErrorException()
        }
    }
}
