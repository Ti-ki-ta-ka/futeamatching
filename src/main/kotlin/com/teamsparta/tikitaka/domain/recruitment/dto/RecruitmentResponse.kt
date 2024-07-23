package com.teamsparta.tikitaka.domain.recruitment.dto

import com.teamsparta.tikitaka.domain.recruitment.model.Recruitment

data class RecruitmentResponse(
    val userId: Long,
    val teamId: Long,
    val recruitType: String,
    val quantity: Int,
    val content: String
) {

    companion object {
        fun from(recruitment: Recruitment) = RecruitmentResponse(
            userId = recruitment.userId,
            teamId = recruitment.teamId,
            recruitType = recruitment.recruitType.toString(),
            quantity = recruitment.quantity,
            content = recruitment.content
        )
    }
}