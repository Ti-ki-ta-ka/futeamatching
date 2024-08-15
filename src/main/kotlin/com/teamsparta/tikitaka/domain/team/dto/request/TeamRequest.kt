package com.teamsparta.tikitaka.domain.team.dto.request

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.team.model.Team

data class TeamRequest(
    val name: String,
    val description: String,
    val region: Region
)

fun TeamRequest.toEntity(
    userId: Long
): Team {
    return Team(
        name = this.name,
        userId = userId,
        description = this.description,
        region = this.region
    )
}