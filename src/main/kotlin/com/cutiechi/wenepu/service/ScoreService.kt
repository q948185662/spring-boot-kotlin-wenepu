package com.cutiechi.wenepu.service

interface ScoreService {
    fun getSemesterList(webToken: String): String

    fun getScores(semester: String, webToken: String): String

    fun getScoreDetail(scoreDetailUrl: String, webToken: String): String
}
