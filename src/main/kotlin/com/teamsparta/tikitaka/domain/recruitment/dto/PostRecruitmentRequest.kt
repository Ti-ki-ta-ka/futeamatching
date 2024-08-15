package com.teamsparta.tikitaka.domain.recruitment.dto

data class PostRecruitmentRequest(
    val recruitType: String,
    val quantity: Int,
    val content: String,
)
