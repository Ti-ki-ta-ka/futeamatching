package com.teamsparta.tikitaka.domain.match.service.v3.matchapplication3

import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MatchApplicationResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MatchApplicationsByIdResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MyApplicationsResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.ReplyApplicationRequest
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MatchApplicationService3 {
    fun applyMatch(userId: Long, matchId: Long): MatchApplicationResponse

    fun replyMatchApplication(
        userId: Long,
        matchId: Long,
        applicationId: Long,
        request: ReplyApplicationRequest
    ): MatchApplicationResponse

    fun getMyApplications(userId: Long): List<MyApplicationsResponse>
    fun cancelMatchApplication(
        principal: UserPrincipal, matchId: Long, applicationId: Long
    )

    fun getMatchApplications(
        principal: UserPrincipal, matchId: Long, pageable: Pageable, approveStatus: String?
    ): Page<MatchApplicationsByIdResponse>

}