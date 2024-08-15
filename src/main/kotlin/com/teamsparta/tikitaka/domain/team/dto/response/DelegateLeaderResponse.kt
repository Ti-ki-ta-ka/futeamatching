package com.teamsparta.tikitaka.domain.team.dto.response

import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole

class DelegateLeaderResponse(
    val teamMemberId: Long,
    val changedRole: TeamRole,
    val message: String,

    ) {
    companion object {
        fun from(teamMember: TeamMember) = DelegateLeaderResponse(
            teamMemberId = teamMember.id!!,
            changedRole = teamMember.teamRole,
            message = "리더 위임 성공"
        )
    }

}