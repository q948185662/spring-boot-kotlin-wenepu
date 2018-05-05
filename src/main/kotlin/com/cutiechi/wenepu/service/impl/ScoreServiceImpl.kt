package com.cutiechi.wenepu.service.impl

import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.exception.TokenErrorException
import com.cutiechi.wenepu.service.ScoreService
import com.cutiechi.wenepu.util.JsonUtil
import org.jsoup.Jsoup

import org.springframework.stereotype.Service

@Service
class ScoreServiceImpl : ScoreService {

    override fun getSemesterList(webToken: String): String {
        val connection = Jsoup.connect("http://jwgl.nepu.edu.cn/tkglAction.do")
                .data("method", "kbxxXs")
                .cookie("JSESSIONID", webToken)
                .timeout(6000)
        return try {
            val document = connection.get()
            val title = document.title()
            when {
                title != "出错页面" -> {
                    val elements = document.getElementById("xnxqh").select("option")
                    val semesterList = ArrayList<String>()
                    for (element in elements) {
                        val semester = element.html()
                        when (semester) {
                            "---请选择---" -> semesterList.add("全部学期")
                            else -> semesterList.add(semester)
                        }
                    }
                    JsonUtil.toJson(semesterList)
                }
                else -> throw TokenErrorException()
            }
        } catch (exception: Exception) {
            throw ServerErrorException()
        }
    }
}
