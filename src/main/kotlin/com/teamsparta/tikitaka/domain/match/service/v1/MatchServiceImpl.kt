package com.teamsparta.tikitaka.domain.match.service.v1

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.match.dto.MatchResponse
import com.teamsparta.tikitaka.domain.match.dto.PostMatchRequest
import com.teamsparta.tikitaka.domain.match.dto.UpdateMatchRequest
import com.teamsparta.tikitaka.domain.match.model.Match
import com.teamsparta.tikitaka.domain.match.model.SortCriteria
import com.teamsparta.tikitaka.domain.match.repository.MatchRepository
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import com.teamsparta.tikitaka.domain.team.repository.teamMember.TeamMemberRepository
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class MatchServiceImpl(
    private val matchRepository: MatchRepository,
    private val teamRepository: TeamRepository,
    private val teamMemberRepository: TeamMemberRepository,
) : MatchService {

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

        val match = matchRepository.findByIdOrNull(matchId)
            ?: throw ModelNotFoundException("match", matchId)


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
        val match = matchRepository.findByIdOrNull(matchId)
            ?: throw ModelNotFoundException("match", matchId)

        if (match.userId != principal.id && !principal.authorities.contains(SimpleGrantedAuthority("ROLE_LEADER"))) throw AccessDeniedException(
            "You do not have permission to delete."
        )

        match.softDelete()

        return MatchResponse.from(match)
    }

    override fun getMatches(pageable: Pageable): Page<MatchResponse> {
        return matchRepository.findAll(pageable)
            .map { match -> MatchResponse.from(match) }
    }

    override fun getAvailableMatchesAndSort(pageable: Pageable, sortCriteria: SortCriteria): Page<MatchResponse> {
        return matchRepository.getAvailableMatchesAndSort(pageable, sortCriteria)
    }

    override fun getMatchesByRegionAndSort(
        region: Region,
        pageable: Pageable,
        sortCriteria: SortCriteria
    ): Page<MatchResponse> {
        return matchRepository.getMatchesByRegionAndSort(region, pageable, sortCriteria)
    }

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

}
