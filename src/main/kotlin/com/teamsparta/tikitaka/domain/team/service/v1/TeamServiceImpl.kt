package com.teamsparta.tikitaka.domain.team.service.v1

import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.team.dto.request.TeamRequest
import com.teamsparta.tikitaka.domain.team.dto.request.toEntity
import com.teamsparta.tikitaka.domain.team.dto.response.PageResponse
import com.teamsparta.tikitaka.domain.team.dto.response.TeamResponse
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import com.teamsparta.tikitaka.domain.team.repository.teammember.TeamMemberRepository
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TeamServiceImpl(
    private val teamRepository: TeamRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val usersRepository: UsersRepository
) : TeamService {

    override fun searchTeamListByName(
        region: String?,
        page: Int,
        size: Int,
        sortBy: String,
        direction: String,
        name: String
    ): PageResponse<TeamResponse> {
        val sortDirection = getDirection(direction)
        val pageable: Pageable = PageRequest.of(page, size, sortDirection, sortBy)
        val pageContent = teamRepository.findTeamsByName(pageable, name, region)



        return PageResponse(
            pageContent.content.map { TeamResponse.from(it) },
            page,
            size,
            pageContent.totalPages
        )
    }

    @Transactional
    override fun createTeam(
        principal: UserPrincipal,
        request: TeamRequest
    ): TeamResponse {
        val user = usersRepository.findByIdOrNull(principal.id) ?: throw ModelNotFoundException("User", principal.id)
        if (user.teamStatus) throw IllegalStateException("유저는 하나의 팀에 소속될 수 있습니다.")
        user.teamStatus = true

        val team = request.toEntity(principal.id)
        return TeamResponse.from(teamRepository.save(team))
            .also {
                teamMemberRepository.save(
                    TeamMember(
                        userId = principal.id,
                        team = team,
                        teamRole = TeamRole.LEADER,
                        createdAt = LocalDateTime.now(),
                    )
                )
            }
    }


    @Transactional
    override fun updateTeam(
        userId: Long,
        request: TeamRequest,
        teamId: Long
    ): TeamResponse {
        val team = teamRepository.findByIdOrNull(teamId) ?: throw ModelNotFoundException("team", teamId)
        val user = teamMemberRepository.findByIdOrNull(userId) ?: throw ModelNotFoundException("user", userId)

        if (user.team != team) throw IllegalStateException("팀 수정 권한이 없습니다.")

        team.updateTeam(request.name, request.description, request.region)
        return TeamResponse.from(team)
    }

    @Transactional
    override fun deleteTeam(
        userId: Long,
        teamId: Long
    ) {
        val team = teamRepository.findByIdOrNull(teamId) ?: throw ModelNotFoundException("team", teamId)
        val teamMember = teamMemberRepository.findByUserId(userId)
        if (teamMember.team != team) throw IllegalStateException("팀 삭제 권한이 없습니다.")
        val teamMembers = teamMemberRepository.findAllByTeamId(teamId)
        teamMembers.forEach { member ->
            val memberUser = usersRepository.findByIdOrNull(member.userId)
                ?: throw ModelNotFoundException("user", member.userId)
            memberUser.teamStatus = false
            usersRepository.save(memberUser)

            member.softDelete()
            teamMemberRepository.save(member)
        }
        team.softDelete()
        teamRepository.save(team)
    }

    override fun getTeams(
        region: String?,
        page: Int,
        size: Int,
        sortBy: String,
        direction: String
    ): PageResponse<TeamResponse> {
        val sortDirection = getDirection(direction)
        val pageable: Pageable = PageRequest.of(page, size, sortDirection, sortBy)
        val pageContent = teamRepository.findAllByPageable(pageable, region)
        return PageResponse(
            pageContent.content.map { TeamResponse.from(it) },
            page,
            size,
            pageContent.totalPages
        )
    }

    override fun getTeam(
        teamId: Long
    ): TeamResponse {
        val team = teamRepository.findByIdOrNull(teamId) ?: throw ModelNotFoundException("team", teamId)
        return TeamResponse.from(team)
    }

    private fun getDirection(direction: String) = when (direction) {
        "asc" -> Sort.Direction.ASC
        else -> Sort.Direction.DESC
    }
}