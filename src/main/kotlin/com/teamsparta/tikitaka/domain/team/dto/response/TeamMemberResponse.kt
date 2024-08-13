package com.teamsparta.tikitaka.domain.team.dto.response

import com.teamsparta.tikitaka.domain.team.model.Team
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.users.dto.UserDto
import java.time.LocalDateTime

data class TeamMemberResponse(
    val teamMemberId: Long,
    val userId: Long,
    val team: Team,
    val teamRole: TeamRole,
    val createdAt: LocalDateTime,
) {

    companion object {
        fun of(teamMember: TeamMember) = TeamMemberResponse(
            teamMemberId = teamMember.id!!,
            userId = teamMember.userId,
            team = teamMember.team,
            teamRole = teamMember.teamRole,
            createdAt = teamMember.createdAt,

            )

        fun from (userId:Long,
                  email: String,
                  name: String
        ) = UserDto(
            userId = userId,
            email = email,
            name = name
        )
    }


}
