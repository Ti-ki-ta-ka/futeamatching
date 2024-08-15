package com.teamsparta.tikitaka.domain.match.service.v1

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.match.dto.MatchResponse
import com.teamsparta.tikitaka.domain.match.dto.PostMatchRequest
import com.teamsparta.tikitaka.domain.match.dto.UpdateMatchRequest
import com.teamsparta.tikitaka.domain.match.model.SortCriteria
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MatchService {

    fun postMatch(principal: UserPrincipal, request: PostMatchRequest): MatchResponse
    fun updateMatch(principal: UserPrincipal, matchId: Long, request: UpdateMatchRequest): MatchResponse
    fun deleteMatch(principal: UserPrincipal, matchId: Long): MatchResponse
    fun getMatches(pageable: Pageable): Page<MatchResponse>
    fun getAvailableMatchesAndSort(pageable: Pageable, sortCriteria: SortCriteria): Page<MatchResponse>
    fun getMatchesByRegionAndSort(region: Region, pageable: Pageable, sortCriteria: SortCriteria): Page<MatchResponse>
    fun getMatchDetails(matchId: Long): MatchResponse
    fun searchMatch(pageable: Pageable, keyword: String, sortCriteria: SortCriteria): Page<MatchResponse>

}
