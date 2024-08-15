package com.teamsparta.tikitaka.domain.team.dto.response

import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember

data class RemoveMemberResopnse(
    val teamMemberId: Long,
    val message: String,
) {

    companion object {
        fun from(teamMember: TeamMember) = RemoveMemberResopnse(
            teamMemberId = teamMember.id!!,
            message = "탈퇴 성공"
        )
    }
}
