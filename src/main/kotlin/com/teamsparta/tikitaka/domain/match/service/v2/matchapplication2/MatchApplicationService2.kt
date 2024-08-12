package com.teamsparta.tikitaka.domain.match.service.v2.matchapplication2

import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MatchApplicationResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MatchApplicationsByIdResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MyApplicationsResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.ReplyApplicationRequest
import com.teamsparta.tikitaka.domain.match.model.SuccessMatch
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MatchApplicationService2 {
    fun applyMatch(userId: Long, matchId: Long): MatchApplicationResponse

    fun replyMatchApplication(
        userId: Long,
        matchId: Long,
        applicationId: Long,
        request: ReplyApplicationRequest
    ): MatchApplicationResponse

    fun successMatch(
        matchId: Long,
        applicationId: Long,
    ): SuccessMatch

    fun getMyApplications(userId: Long): List<MyApplicationsResponse>
    fun cancelMatchApplication(
        principal: UserPrincipal, matchId: Long, applicationId: Long
    )

    fun getMatchApplications(
        principal: UserPrincipal, matchId: Long, pageable: Pageable, approveStatus: String?
    ): Page<MatchApplicationsByIdResponse>
}