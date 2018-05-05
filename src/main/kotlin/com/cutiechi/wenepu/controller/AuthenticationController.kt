package com.cutiechi.wenepu.controller

import com.cutiechi.wenepu.exception.AuthenticationErrorException
import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.service.AuthenticationService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/authentication")
@CrossOrigin
class AuthenticationController {

    @Autowired
    private lateinit var service: AuthenticationService

    @PostMapping("/app-token", produces = ["application/json;charset=UTF-8"])
    fun getAppToken(userName: String, password: String): String {
        return try {
            val appToken = service.getAppToken(userName, password)
            """
            {
                "code": 200,
                "message": "获取 app token 成功！",
                "appToken": "$appToken"
            }
            """.trimIndent()
        } catch (exception: AuthenticationErrorException) {
            """
            {
                "code": 401,
                "message": "用户名或密码错误，获取 app token 失败！"
            }
            """.trimIndent()
        } catch (exception: ServerErrorException) {
            """
            {
                "code": 500,
                "message": "服务器错误，获取 app token 失败！"
            }
            """.trimIndent()
        }
    }

    @PostMapping("/web-token", produces = ["application/json;charset=UTF-8"])
    fun getWebToken(userName: String, password: String): String {
        return try {
            val webToken = service.getWebToken(userName, password)
            """
            {
                "code": 200,
                "message": "获取 web token 成功！",
                "appToken": "$webToken"
            }
            """.trimIndent()
        } catch (exception: AuthenticationErrorException) {
            """
            {
                "code": 401,
                "message": "用户名或密码错误，获取 web token 失败！"
            }
            """.trimIndent()
        } catch (exception: ServerErrorException) {
            """
            {
                "code": 500,
                "message": "服务器错误，获取 web token 失败！"
            }
            """.trimIndent()
        }
    }
}
