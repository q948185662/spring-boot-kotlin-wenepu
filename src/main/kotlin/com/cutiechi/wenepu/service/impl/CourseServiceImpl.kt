package com.cutiechi.wenepu.service.impl

import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.exception.TokenErrorException
import com.cutiechi.wenepu.model.CourseItem
import com.cutiechi.wenepu.service.CourseService
import com.cutiechi.wenepu.util.JsonUtil

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class CourseServiceImpl : CourseService {

    override fun getCourseTable(
            semester: String,
            weekly: String,
            userName: String,
            webToken: String
    ): String {
        val connection = Jsoup.connect("http://jwgl.nepu.edu.cn/tkglAction.do")
                .data("method", "goListKbByXs")
                .data("xnxqh", semester)
                .data("zc", weekly)
                .data("xs0101id", userName)
                .cookie("JSESSIONID", webToken)
                .timeout(6000)
        return try {
            val document = connection.post()
            val title = document.title()
            when {
                title != "出错页面" -> {
                    val table = document.getElementById("kbtable")
                    val trs = table.select("tr")
                    val oneWeekCourseMap = HashMap<Int, ArrayList<CourseItem?>>()
                    for (tr in 1..6) {
                        val tds = trs.get(tr).select("td")
                        val sameTimeCourseList = ArrayList<CourseItem?>()
                        for (td in 1..7) {
                            val html = tds.get(td).select("div").get(1).html()
                            if (html == "&nbsp;") {
                                sameTimeCourseList.add(null)
                            } else {
                                val courseInfo = html.replace("\n", "")
                                        .replace(" ", "")
                                        .replace("&nbsp;", "")
                                        .replace("<br>", "_")
                                        .replace("<nobr>", "")
                                        .replace("</nobr>", "")
                                        .split("_")
                                val courseName = courseInfo.get(0)
                                val courseClass = courseInfo.get(1)
                                val courseTeacher = courseInfo.get(2)
                                val courseWeekly = courseInfo.get(3)
                                val courseRoom = courseInfo.get(4)
                                sameTimeCourseList.add(CourseItem(courseName
                                        , courseClass
                                        , courseTeacher
                                        , courseWeekly
                                        , courseRoom)
                                )
                            }
                        }
                        oneWeekCourseMap[tr] = sameTimeCourseList
                    }
                    JsonUtil.toJson(oneWeekCourseMap)
                }
                else -> throw TokenErrorException()
            }
        } catch (exception: Exception) {
            throw ServerErrorException()
        }
    }
}
