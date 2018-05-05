package com.cutiechi.wenepu.service

interface CourseService {
    fun getCourseTable(
            semester: String,
            weekly: String,
            userName: String,
            webToken: String
    ): String
}
