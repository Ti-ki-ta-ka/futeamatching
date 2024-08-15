package com.teamsparta.tikitaka.domain.team.dto.response

import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole

data class ReassignRoleResponse(

    val teamMemberId: Long,
    val changedRole: TeamRole,
    val message: String,

    ) {
    companion object {
        fun from(teamMember: TeamMember) = ReassignRoleResponse(
            teamMemberId = teamMember.id!!,
            changedRole = teamMember.teamRole,
            message = "권한 부여 성공"
        )
    }


}
