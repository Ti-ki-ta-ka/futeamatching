package com.teamsparta.tikitaka.domain.team.service.v2.teammember

import com.teamsparta.tikitaka.domain.team.dto.response.RemoveMemberResopnse
import com.teamsparta.tikitaka.domain.team.dto.response.TeamMemberResponse

interface TeamMemberService2 {
    fun getMyTeamMembers(userId: Long): List<TeamMemberResponse>
    fun leaveTeam(userId: Long, teamMemberId: Long): RemoveMemberResopnse
}