package com.cutiechi.wenepu.util

import com.google.gson.Gson
import com.google.gson.JsonParser

object JsonUtil {
    private val gson = Gson()

    fun getValue(json: String?, key: String): String = JsonParser().parse(json).asJsonObject.get(key).asString

    fun toJson(src: Any): String = gson.toJson(src)
}
