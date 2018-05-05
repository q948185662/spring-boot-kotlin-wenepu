package com.cutiechi.wenepu.controller

import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.exception.TokenErrorException
import com.cutiechi.wenepu.service.CourseService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestHeader

@RestController
@CrossOrigin
@RequestMapping("/courses")
class CourseController {

    @Autowired
    private lateinit var service: CourseService

    @PostMapping("/table", produces = ["application/json;charset=UTF-8"])
    fun getCourseTable(
            semester: String,
            weekly: String,
            userName: String,
            @RequestHeader("webToken") webToken: String
    ): String = try {
        val courseTable = service.getCourseTable(
                semester,
                weekly,
                userName,
                webToken
        )
        """
        {
            "code": 200,
            "message": "获取课程表成功！",
            "courseTable": $courseTable
        }
        """.trimIndent()
    } catch (exception: TokenErrorException) {
        """
        {
            "code": 403,
            "message": "web token 错误，获取课程表失败！"
        }
        """.trimIndent()
    } catch (exception: ServerErrorException) {
        """
        {
            "code": 500,
            "message": "服务器错误，获取课程表失败！"
        }
        """.trimIndent()
    }
}
