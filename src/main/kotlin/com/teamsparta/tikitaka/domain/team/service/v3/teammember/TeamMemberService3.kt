package com.teamsparta.tikitaka.domain.team.service.v3.teammember

import com.teamsparta.tikitaka.domain.team.dto.response.RemoveMemberResopnse
import com.teamsparta.tikitaka.domain.team.dto.response.TeamMemberResponse

interface TeamMemberService3 {
    fun getMyTeamMembers(userId: Long): List<TeamMemberResponse>
    fun leaveTeam(userId: Long, teamMemberId: Long): RemoveMemberResopnse
}