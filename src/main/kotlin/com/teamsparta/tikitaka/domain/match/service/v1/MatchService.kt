package com.teamsparta.tikitaka.domain.match.service.v1

import com.teamsparta.tikitaka.domain.match.dto.MatchResponse
import com.teamsparta.tikitaka.domain.match.dto.MatchStatusResponse
import com.teamsparta.tikitaka.domain.match.dto.PostMatchRequest
import com.teamsparta.tikitaka.domain.match.dto.UpdateMatchRequest

interface MatchService {

    fun postMatch(request: PostMatchRequest): MatchStatusResponse
    fun updateMatch(matchId: Long, request: UpdateMatchRequest): MatchStatusResponse

    fun deleteMatch(matchId: Long): MatchStatusResponse

    fun getMatches(): List<MatchResponse>

    fun getMatchDetails(matchId: Long): MatchResponse


}