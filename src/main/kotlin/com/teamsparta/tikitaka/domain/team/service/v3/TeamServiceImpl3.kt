package com.teamsparta.tikitaka.domain.team.service.v3

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.team.dto.request.TeamRequest
import com.teamsparta.tikitaka.domain.team.dto.request.toEntity
import com.teamsparta.tikitaka.domain.team.dto.response.PageResponse
import com.teamsparta.tikitaka.domain.team.dto.response.TeamRankResponse
import com.teamsparta.tikitaka.domain.team.dto.response.TeamResponse
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import com.teamsparta.tikitaka.domain.team.repository.teamMember.TeamMemberRepository
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.aop.StopWatch
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TeamServiceImpl3(
    private val teamRepository: TeamRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val usersRepository: UsersRepository
) : TeamService3 {

    override fun searchTeamListByName(
        region: String?, page: Int, size: Int, sortBy: String, direction: String, name: String
    ): PageResponse<TeamResponse> {
        val sortDirection = getDirection(direction)
        val pageable: Pageable = PageRequest.of(page, size, sortDirection, sortBy)
        val pageContent = teamRepository.findByName(pageable, name, region)



        return PageResponse(
            pageContent.content.map { TeamResponse.from(it) }, page, size, pageContent.totalPages
        )
    }

    @Transactional
    override fun createTeam(
        principal: UserPrincipal, request: TeamRequest
    ): TeamResponse {
        val user = usersRepository.findByIdOrNull(principal.id) ?: throw ModelNotFoundException("User", principal.id)
        if (user.teamStatus) throw IllegalStateException("유저는 하나의 팀에 소속될 수 있습니다.")
        user.teamStatus = true


        val team = request.toEntity(principal.id)
        return TeamResponse.from(teamRepository.save(team)).also {
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
        userId: Long, request: TeamRequest, teamId: Long
    ): TeamResponse {
        val team = teamRepository.findByIdOrNull(teamId) ?: throw ModelNotFoundException("team", teamId)
        val teamMember = teamMemberRepository.findByUserId(userId)

        if (teamMember.team != team) throw IllegalStateException("팀 수정 권한이 없습니다.")

        team.updateTeam(request.name, request.description, request.region)
        return TeamResponse.from(team)
    }

    @Transactional
    override fun deleteTeam(
        userId: Long, teamId: Long
    ) {
        val team = teamRepository.findByIdOrNull(teamId) ?: throw ModelNotFoundException("team", teamId)
        val teamMember = teamMemberRepository.findByUserId(userId)
        val user = usersRepository.findByIdOrNull(userId) ?: throw ModelNotFoundException("user", userId)
        if (teamMember.team != team) throw IllegalStateException("팀 삭제 권한이 없습니다.")
        user.teamStatus = false
        team.softDelete()
    }

    @StopWatch
    @Cacheable("getTeams")
    override fun getTeams(
        region: String?, page: Int, size: Int, sortBy: String, direction: String
    ): PageResponse<TeamResponse> {
        val sortDirection = getDirection(direction)
        val pageable: Pageable = PageRequest.of(page, size, sortDirection, sortBy)
        val pageContent = teamRepository.findAllByPageable(pageable, region)
        return PageResponse(
            pageContent.content.map { TeamResponse.from(it) }, page, size, pageContent.totalPages
        )

    }

    override fun getTeam(
        teamId: Long
    ): TeamResponse {
        val team = teamRepository.findByIdOrNull(teamId) ?: throw ModelNotFoundException("team", teamId)
        return TeamResponse.from(team)
    }

    @Cacheable("teamRankRedis", cacheManager = "redisCacheManager")
    override fun getTeamRanks(region: Region?, page: Int, size: Int): Page<TeamRankResponse> {
        val fixedPage = page.coerceAtMost(9)
        val fixedSize = size.coerceAtMost(10)

        val sort = if (region != null) {
            Sort.by(Sort.Direction.ASC, "regionRank")
        } else {
            Sort.by(Sort.Direction.ASC, "rank")
        }

        val pageable = PageRequest.of(fixedPage, fixedSize, sort)

        val teams = if (region != null) {
            teamRepository.findByRegionAndRankIsNotNull(region, pageable)
        } else {
            teamRepository.findAllByRankIsNotNull(pageable)
        }

        return teams.map { team ->
            TeamRankResponse(
                id = team.id!!,
                name = team.name,
                tierScore = team.tierScore,
                rank = team.rank,
                regionRank = team.regionRank,
                region = team.region.name
            )
        }
    }

    private fun getDirection(direction: String) = when (direction) {
        "asc" -> Sort.Direction.ASC
        else -> Sort.Direction.DESC
    }
}