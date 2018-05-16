package com.cutiechi.wenepu.controller

import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.exception.TokenErrorException
import com.cutiechi.wenepu.service.TimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/time")
@RestController
class TimeController {

    @Autowired
    private lateinit var service: TimeService

    @PostMapping("/current", produces = ["application/json;charset=UTF-8"])
    fun getCurrentWeek(
            currentDate: String,
            @RequestHeader("appToken") appToken: String
    ) = try {
        val currentWeek = service.getCurrentWeek(currentDate, appToken)
        """
        {
            "code": 200,
            "message": "获取当前周成功！",
            "currentTime": $currentWeek
        }
        """.trimIndent()
    } catch (exception: TokenErrorException) {
        """
            {
                "code": 403,
                "message": "app token 错误，获取当前周失败！"
            }
            """.trimIndent()
    } catch (exception: ServerErrorException) {
        """
            {
                "code": 500,
                "message": "服务器错误，获取当前周失败！"
            }
            """.trimIndent()
    }
}
