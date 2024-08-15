package com.teamsparta.tikitaka.domain.match.service.v2

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.match.dto.MatchResponse
import com.teamsparta.tikitaka.domain.match.dto.MyTeamMatchResponse
import com.teamsparta.tikitaka.domain.match.dto.PostMatchRequest
import com.teamsparta.tikitaka.domain.match.dto.UpdateMatchRequest
import com.teamsparta.tikitaka.domain.match.model.Match
import com.teamsparta.tikitaka.domain.match.model.SortCriteria
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface MatchService2 {

    fun postMatch(principal: UserPrincipal, request: PostMatchRequest): MatchResponse
    fun updateMatch(principal: UserPrincipal, matchId: Long, request: UpdateMatchRequest, match: Match): MatchResponse
    fun deleteMatch(principal: UserPrincipal, matchId: Long, match: Match): MatchResponse
    fun getMatches(pageable: Pageable): Page<MatchResponse>
    fun getAvailableMatchesAndSort(pageable: Pageable, sortCriteria: SortCriteria): Page<MatchResponse>
    fun getMatchesByDateAndRegion(pageable: Pageable, matchDate: LocalDate, region: List<Region>?): Page<MatchResponse>
    fun getMatchesByRegionAndSort(
        region: List<Region>,
        pageable: Pageable,
        sortCriteria: SortCriteria
    ): Page<MatchResponse>

    fun getMatchDetails(matchId: Long): MatchResponse
    fun searchMatch(pageable: Pageable, keyword: String, sortCriteria: SortCriteria): Page<MatchResponse>
    fun getMyTeamMatches(
        principal: UserPrincipal,
        pageable: Pageable,
        matchStatus: Boolean?
    ): Page<MyTeamMatchResponse>

    fun getMatch(matchId: Long): Match
}
