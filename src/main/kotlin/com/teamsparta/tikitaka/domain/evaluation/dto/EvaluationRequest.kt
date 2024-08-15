package com.teamsparta.tikitaka.domain.evaluation.dto

data class EvaluationRequest(
    val skillScore: Int,
    val mannerScore: Int,
    val attendanceScore: Int
)
