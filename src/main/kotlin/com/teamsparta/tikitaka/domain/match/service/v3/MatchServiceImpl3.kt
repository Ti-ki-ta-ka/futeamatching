package com.teamsparta.tikitaka.domain.match.service.v3

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.match.dto.MatchResponse
import com.teamsparta.tikitaka.domain.match.dto.MyTeamMatchResponse
import com.teamsparta.tikitaka.domain.match.dto.PostMatchRequest
import com.teamsparta.tikitaka.domain.match.dto.UpdateMatchRequest
import com.teamsparta.tikitaka.domain.match.model.Match
import com.teamsparta.tikitaka.domain.match.model.SortCriteria
import com.teamsparta.tikitaka.domain.match.repository.MatchRepository
import com.teamsparta.tikitaka.domain.match.repository.matchapplication.MatchApplicationRepository
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import com.teamsparta.tikitaka.domain.team.repository.teamMember.TeamMemberRepository
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.aop.StopWatch
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate


@Service
class MatchServiceImpl3(
    private val matchRepository: MatchRepository,
    private val teamRepository: TeamRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val usersRepository: UsersRepository,
    private val matchApplicationRepository: MatchApplicationRepository,
) : MatchService3 {

    @Transactional
    override fun postMatch(
        principal: UserPrincipal,
        request: PostMatchRequest,
    ): MatchResponse {

        val teamMember = teamMemberRepository.findByUserId(principal.id)
        val teamId = teamMember.team.id

        val match = matchRepository.save(
            Match.of(
                title = request.title.trim(),
                matchDate = request.matchDate,
                location = request.location.trim(),
                content = request.content.trim(),
                matchStatus = false,
                teamId = teamId!!,
                userId = principal.id,
                region = Region.fromString(request.region.trim()),
            )
        )

        val team = teamRepository.findByIdOrNull(teamId)
            ?: throw ModelNotFoundException("team", teamId)

        return MatchResponse.from(match)
    }

    @Transactional
    override fun updateMatch(
        principal: UserPrincipal,
        matchId: Long,
        request: UpdateMatchRequest,
    ): MatchResponse {

        val match = findMatchById(matchId)


        if (match.userId != principal.id && !principal.authorities.contains(SimpleGrantedAuthority("ROLE_LEADER")))
            throw AccessDeniedException(
                "You do not have permission to update."
            )

        match.updateMatch(request)

        return MatchResponse.from(match)
    }

    @Transactional
    override fun deleteMatch(
        principal: UserPrincipal,
        matchId: Long,
    ): MatchResponse {

        val match = findMatchById(matchId)
        if (match.userId != principal.id && !principal.authorities.contains(SimpleGrantedAuthority("ROLE_LEADER"))) throw AccessDeniedException(
            "You do not have permission to delete."
        )
        match.softDelete()
        deleteRelatedApplications(matchId)

        return MatchResponse.from(match)
    }

    private fun deleteRelatedApplications(matchId: Long) {
        val applications = matchApplicationRepository.findByMatchPostId(matchId)
        applications?.forEach { it.delete() }
    }


    @StopWatch
    @Cacheable("getMatchesByDateAndRegion")
    override fun getMatchesByDateAndRegion(
        pageable: Pageable,
        matchDate: LocalDate,
        regions: List<Region>?
    ): Page<MatchResponse> {
        val startOfDay = matchDate.atStartOfDay()
        val endOfDay = matchDate.atTime(23, 59, 59, 999)
        return matchRepository.findByDateAndRegions(startOfDay, endOfDay, regions, pageable)
            .map { match -> MatchResponse.from(match) }
    }

    @Cacheable("getAvailableMatchesAndSort")
    override fun getAvailableMatchesAndSort(pageable: Pageable, sortCriteria: SortCriteria): Page<MatchResponse> {
        return matchRepository.getAvailableMatchesAndSort(pageable, sortCriteria)
    }

    @Cacheable("getMatchesByRegionAndSort")
    override fun getMatchesByRegionAndSort(
        region: List<Region>,
        pageable: Pageable,
        sortCriteria: SortCriteria
    ): Page<MatchResponse> {
        return matchRepository.getMatchesByRegionsAndSort(region, pageable, sortCriteria)
    }

    @Cacheable("getMatchDetails")
    override fun getMatchDetails(
        matchId: Long
    ): MatchResponse {
        return matchRepository.findByIdOrNull(matchId)
            ?.let { match -> MatchResponse.from(match) }
            ?: throw ModelNotFoundException("match", matchId)
    }

    override fun searchMatch(pageable: Pageable, keyword: String, sortCriteria: SortCriteria): Page<MatchResponse> {
        return matchRepository.searchMatchByPageableAndKeyword(pageable, keyword, sortCriteria)
    }

    override fun getMyTeamMatches(
        principal: UserPrincipal,
        pageable: Pageable,
        matchStatus: Boolean?
    ): Page<MyTeamMatchResponse> {
        usersRepository.findByIdOrNull(principal.id) ?: throw ModelNotFoundException("User", principal.id)

        val teamMember = teamMemberRepository.findByUserId(principal.id)
        val teamId = teamMember.team.id

        val myMatches = matchRepository.findMatchesByTeamId(pageable, teamId!!, matchStatus)
        return myMatches
    }

    private fun findMatchById(matchId: Long) =
        matchRepository.findByIdOrNull(matchId) ?: throw ModelNotFoundException("Match", matchId)

}
