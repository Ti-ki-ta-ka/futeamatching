package com.teamsparta.tikitaka.domain.recruitment.dto

data class UpdateRecruitmentRequest(
    val recruitType: String,
    val quantity: Int,
    val content: String,
)