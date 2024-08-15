package com.teamsparta.tikitaka.domain.team.dto.response

import com.teamsparta.tikitaka.domain.team.model.Team

data class TeamRankResponse(
    val id: Long,
    val name: String,
    val tierScore: Int,
    val rank: Long?,
    val regionRank: Long?,
    val region: String
) {
    fun from(
        team: Team
    ): TeamRankResponse {
        return TeamRankResponse(
            team.id!!,
            team.name,
            team.tierScore,
            team.rank,
            team.regionRank,
            team.region.name,
        )
    }
}

