package com.cutiechi.wenepu.service

interface AuthenticationService {
    fun getAppToken(userName: String, password: String): String

    fun getWebToken(userName: String, password: String): String
}
