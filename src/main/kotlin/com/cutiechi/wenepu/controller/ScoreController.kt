package com.cutiechi.wenepu.controller

import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.exception.TokenErrorException
import com.cutiechi.wenepu.service.ScoreService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
class ScoreController {

    @Autowired
    private lateinit var service: ScoreService

    @GetMapping("/semesters", produces = ["application/json;charset=UTF-8"])
    fun getSemesterList(@RequestHeader("webToken") webToken: String): String {
        return try {
            val semesterList = service.getSemesterList(webToken)
            """
            {
                "code": 200,
                "message": "获取开课学期列表成功!",
                "semesterList": $semesterList
            }
            """.trimIndent()
        } catch (exception: TokenErrorException) {
            """
            {
                "code": 403,
                "message": "web token 错误，获取开课学期列表失败！"
            }
            """.trimIndent()
        } catch (exception: ServerErrorException) {
            """
            {
                "code": 500,
                "message": "服务器错误，获取开课学期列表失败！"
            }
            """.trimIndent()
        }
    }

    @PostMapping("/scores", produces = ["application/json;charset=UTF-8"])
    fun getScores(
            semester: String,
            @RequestHeader("webToken") webToken: String
    ): String = try {
        val scores = service.getScores(semester, webToken)
        """
        {
            "code": 200,
            "message": "获取成绩成功！",
            "scores": $scores
        }
        """.trimIndent()
    } catch (e: TokenErrorException) {
        """
        {
            "code": 403,
            "message": "web token 错误，获取成绩失败！"
        }
        """.trimIndent()
    } catch (e: ServerErrorException) {
        """
        {
            "code": 500,
            "message": "服务器错误，获取成绩失败！"
        }
        """.trimIndent()
    }
}
