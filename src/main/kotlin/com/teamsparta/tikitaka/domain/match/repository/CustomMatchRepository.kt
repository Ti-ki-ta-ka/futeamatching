package com.teamsparta.tikitaka.domain.match.repository

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.match.dto.MatchResponse
import com.teamsparta.tikitaka.domain.match.dto.MyTeamMatchResponse
import com.teamsparta.tikitaka.domain.match.model.Match
import com.teamsparta.tikitaka.domain.match.model.SortCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface CustomMatchRepository {
    fun searchMatchByPageableAndKeyword(
        pageable: Pageable,
        keyword: String?,
        sortCriteria: SortCriteria
    ): Page<MatchResponse>

    fun getAvailableMatchesAndSort(pageable: Pageable, sortCriteria: SortCriteria): Page<MatchResponse>
    fun getMatchesByRegionAndSort(region: Region, pageable: Pageable, sortCriteria: SortCriteria): Page<MatchResponse>
    fun getMatchesByRegionsAndSort(
        region: List<Region>,
        pageable: Pageable,
        sortCriteria: SortCriteria
    ): Page<MatchResponse>

    fun findMatchesByTeamId(
        pageable: Pageable,
        teamId: Long,
        matchStatus: Boolean?
    ): Page<MyTeamMatchResponse>

}