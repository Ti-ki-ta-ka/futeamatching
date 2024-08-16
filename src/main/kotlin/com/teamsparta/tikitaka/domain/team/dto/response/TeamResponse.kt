package com.teamsparta.tikitaka.domain.team.dto.response

import com.teamsparta.tikitaka.domain.team.model.Team
import java.io.Serializable
import java.time.LocalDateTime

data class TeamResponse(
    val id: Long,
    val name: String,
    val description: String,
    val numMember: Int,
    val tierScore: Int,
    val mannerScore: Int,
    val attendanceScore: Int,
    val rank: Long?,
    val recruitStatus: Boolean,
    val region: String,
    val createAt: LocalDateTime?
) : Serializable {
    companion object {

        private const val serialVersionUID = 2L

        fun from(
            team: Team
        ): TeamResponse {
            return TeamResponse(
                team.id ?: throw IllegalStateException("ID cannot be Null"),
                team.name,
                team.description,
                team.countMember,
                team.tierScore,
                team.mannerScore,
                team.attendanceScore,
                team.rank,
                team.recruitStatus,
                team.region.name,
                team.createdAt
            )
        }
    }
}

