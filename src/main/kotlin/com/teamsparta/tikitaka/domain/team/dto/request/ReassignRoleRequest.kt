package com.teamsparta.tikitaka.domain.team.dto.request

import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole

data class ReassignRoleRequest(
    val teamMemberId: Long,
    val role: TeamRole,
)
