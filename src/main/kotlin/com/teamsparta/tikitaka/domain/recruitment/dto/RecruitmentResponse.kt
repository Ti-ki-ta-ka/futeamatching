package com.teamsparta.tikitaka.domain.recruitment.dto

import com.teamsparta.tikitaka.domain.recruitment.model.Recruitment
import com.teamsparta.tikitaka.domain.team.dto.response.TeamResponse
import java.time.LocalDateTime

data class RecruitmentResponse(
    val recruitmentId: Long,
    val userId: Long,
    val teamId: Long,
    val recruitType: String,
    val quantity: Int,
    val content: String,
    val team: TeamResponse,
    val createdAt: LocalDateTime
) {

    companion object {
        fun from(recruitment: Recruitment, teamResponse: TeamResponse) = RecruitmentResponse(
            recruitmentId = recruitment.id!!,
            userId = recruitment.userId,
            teamId = recruitment.teamId,
            recruitType = recruitment.recruitType.toString(),
            quantity = recruitment.quantity,
            content = recruitment.content,
            team = teamResponse,
            createdAt = recruitment.createdAt
        )
    }
}
