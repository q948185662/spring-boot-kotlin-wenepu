package com.cutiechi.wenepu.util

import com.google.gson.JsonParser

object JsonUtil {

    fun getValue(json: String?, key: String): String = JsonParser().parse(json).asJsonObject.get(key).asString
}
