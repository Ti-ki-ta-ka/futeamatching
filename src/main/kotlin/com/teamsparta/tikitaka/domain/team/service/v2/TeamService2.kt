package com.teamsparta.tikitaka.domain.team.service.v2

import com.teamsparta.tikitaka.domain.team.dto.request.TeamRequest
import com.teamsparta.tikitaka.domain.team.dto.response.PageResponse
import com.teamsparta.tikitaka.domain.team.dto.response.TeamResponse
import com.teamsparta.tikitaka.domain.users.dto.UserDto
import com.teamsparta.tikitaka.domain.users.dto.UserResponse
import com.teamsparta.tikitaka.infra.security.UserPrincipal

interface TeamService2 {


    fun createTeam(principal: UserPrincipal, request: TeamRequest): TeamResponse

    fun updateTeam(userId: Long, request: TeamRequest, teamId: Long): TeamResponse

    fun deleteTeam(userId: Long, teamId: Long)

    fun getTeams(region: String?, page: Int, size: Int, sortBy: String, direction: String): PageResponse<TeamResponse>

    fun getTeam(teamId: Long): TeamResponse

    fun searchTeamListByName(
        region: String?,
        page: Int,
        size: Int,
        sortBy: String,
        direction: String,
        name: String
    ): PageResponse<TeamResponse>

    fun getMyTeam(userPrincipal: UserPrincipal): TeamResponse
    fun getMyTeamMembers(userPrincipal: UserPrincipal): List<UserResponse>
}