package com.teamsparta.tikitaka.domain.team.service.v2

import com.teamsparta.tikitaka.domain.team.dto.request.ReassignRoleRequest
import com.teamsparta.tikitaka.domain.team.dto.response.DelegateLeaderResponse
import com.teamsparta.tikitaka.domain.team.dto.response.ReassignRoleResponse
import com.teamsparta.tikitaka.domain.team.dto.response.RemoveMemberResopnse
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import com.teamsparta.tikitaka.infra.security.UserPrincipal


interface LeaderTeamService2 {

    fun reassignRole(principal: UserPrincipal, teamMemberId: Long, request: ReassignRoleRequest): ReassignRoleResponse
    fun delegateLeader(principal: UserPrincipal, teamMemberId: Long): DelegateLeaderResponse
    fun removeMember(principal: UserPrincipal, teamMemberId: Long): RemoveMemberResopnse
    fun addMember(userId: Long, teamId: Long): TeamMember


}
