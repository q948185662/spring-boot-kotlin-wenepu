package com.cutiechi.wenepu.service.impl

import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.exception.TokenErrorException
import com.cutiechi.wenepu.model.ScoreItem
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

    override fun getScores(semester: String, webToken: String): String {
        val connection = Jsoup.connect("http://jwgl.nepu.edu.cn/xszqcjglAction.do?method=queryxscj")
                .data("kksj", semester)
                .cookie("JSESSIONID", webToken)
                .timeout(6000)
        return try {
            val document = connection.post()
            val title = document.title()
            when {
                title != "出错页面" -> {
                    val credit = document.getElementById("tblBm").select("td")[0].select("span")[1].html()
                    var gradePointAverage = document.getElementById("tblBm").select("td")[0].select("span")[3].html()
                    gradePointAverage = gradePointAverage.substring(0, gradePointAverage.length - 1)
                    val elements = document.getElementById("mxh").getAllElements().select("tr")
                    val scoreList = ArrayList<ScoreItem>()
                    for (element in elements) {
                        var courseId = element.select("td")[0].html()
                        courseId = courseId.substring(courseId.indexOf(";") + 1)
                        val courseSemester = element.select("td")[3].html()
                        val courseName = element.select("td")[4].html()
                        val courseScore = element.select("td")[5].select("a")[0].html()
                        var scoreDetailUrl = element.select("td")[5].select("a")[0].attr("onclick")
                        scoreDetailUrl = scoreDetailUrl.substring(scoreDetailUrl.indexOf("/"), scoreDetailUrl.lastIndexOf("'"))
                        var scoreSign = element.select("td")[6].html()
                        scoreSign = scoreSign.substring(scoreSign.indexOf(";") + 1)
                        val courseNature = element.select("td")[7].html()
                        val courseType = element.select("td")[8].html()
                        val courseHour = element.select("td")[9].html()
                        val courseCredit = element.select("td")[10].html()
                        val examNature = element.select("td")[11].html()
                        var supplementSemester = element.select("td")[12].html()
                        supplementSemester = supplementSemester.substring(supplementSemester.indexOf(";") + 1)
                        scoreList.add(ScoreItem(
                                courseId,
                                courseSemester,
                                courseName,
                                courseScore,
                                scoreDetailUrl,
                                scoreSign,
                                courseNature,
                                courseType,
                                courseHour,
                                courseCredit,
                                examNature,
                                supplementSemester
                        ))
                    }
                    """
                    {
                        "credit": "$credit",
                        "gradePointAverage": "$gradePointAverage",
                        "scoreList": ${JsonUtil.toJson(scoreList)}
                    }
                    """.trimIndent()
                }
                else -> throw TokenErrorException()
            }
        } catch (exception: Exception) {
            throw ServerErrorException()
        }
    }

    override fun getScoreDetail(
            scoreDetailUrl: String,
            webToken: String
    ): String {
        val connection = Jsoup.connect("http://jwgl.nepu.edu.cn$scoreDetailUrl")
                .cookie("JSESSIONID", webToken)
                .timeout(6000)
        return try {
            val document = connection.post()
            val title = document.title()
            when {
                title != "出错页面" -> {
                    val regular = document.getElementById("mxh").select("td")[0].text()
                    val regularProportion = document.getElementById("mxh").select("td")[1].text()
                    val midterm = document.getElementById("mxh").select("td")[2].text()
                    val midtermProportion = document.getElementById("mxh").select("td")[3].text()
                    val endterm = document.getElementById("mxh").select("td")[4].text()
                    val endtermProportion = document.getElementById("mxh").select("td")[5].text()
                    val total = document.getElementById("mxh").select("td")[6].text()
                    """
                    {
                        "regular": "$regular",
                        "regularProportion": "$regularProportion",
                        "midterm": "$midterm",
                        "midtermProportion": "$midtermProportion",
                        "endterm": "$endterm",
                        "endtermProportion": "$endtermProportion",
                        "total": "$total"
                    }
                    """.trimIndent()
                }
                else -> throw TokenErrorException()
            }
        } catch (exception: java.lang.Exception) {
            throw ServerErrorException()
        }
    }
}
