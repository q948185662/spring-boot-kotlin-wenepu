package com.cutiechi.wenepu.controller

import com.cutiechi.wenepu.exception.ServerErrorException
import com.cutiechi.wenepu.exception.TokenErrorException
import com.cutiechi.wenepu.service.ClassroomService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@CrossOrigin
@RequestMapping("/classrooms")
class ClassroomController {

    @Autowired
    private lateinit var service: ClassroomService

    @PostMapping("/free", produces = ["application/json;charset=UTF-8"])
    fun getFreeClassRoomList(
            freeDate: String,
            freeTime: String,
            teachingBuildingId: String,
            seatNumber: String,
            @RequestHeader("appToken") appToken: String
    ): String = try {
        val freeClassroomList = service.getFreeClassroomList(
                freeDate,
                freeTime,
                teachingBuildingId,
                seatNumber,
                appToken
        )
        """
        {
            "code": 200,
            "message": "获取空教室成功！",
            "freeClassroomList": $freeClassroomList
        }
        """.trimIndent()
    } catch (e: TokenErrorException) {
        """
        {
            "code": 403,
            "message": "app token 错误，获取空教室失败！"
        }
        """.trimIndent()
    } catch (e: ServerErrorException) {
        """
        {
            "code": 500,
            "message": "服务器错误，获取空教室失败！"
        }
        """.trimIndent()
    }
}
