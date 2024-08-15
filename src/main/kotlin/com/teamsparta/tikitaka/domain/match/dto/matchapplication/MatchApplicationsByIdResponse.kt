package com.teamsparta.tikitaka.domain.match.dto.matchapplication

import com.teamsparta.tikitaka.domain.match.model.matchapplication.MatchApplication
import java.time.LocalDateTime

data class MatchApplicationsByIdResponse(
    val id: Long,
    val applyUserName: String,
    val applyTeamName: String,
    val approveStatus: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(
            matchApplication: MatchApplication,
            userName: String,
            teamName: String
        ): MatchApplicationsByIdResponse {
            return MatchApplicationsByIdResponse(
                id = matchApplication.id!!,
                applyUserName = userName,
                applyTeamName = teamName,
                approveStatus = matchApplication.approveStatus.name,
                createdAt = matchApplication.createdAt
            )
        }
    }
}



