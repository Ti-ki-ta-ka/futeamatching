package com.teamsparta.tikitaka.domain.match.repository.matchapplication

import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MatchApplicationsByIdResponse
import com.teamsparta.tikitaka.domain.match.model.matchapplication.MatchApplication
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface CustomMatchApplicationRepository {
    fun findByTeamIdAndMatchDate(teamId: Long, matchDate: LocalDate): List<MatchApplication>
    fun findByApplyTeamId(applyTeamId: Long): List<MatchApplication>
    fun findApplicationsByMatchId(
        pageable: Pageable,
        matchId: Long,
        approveStatus: String?
    ): Page<MatchApplicationsByIdResponse>
}