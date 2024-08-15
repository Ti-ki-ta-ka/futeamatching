package com.teamsparta.tikitaka.domain.match.service.v1.matchapplication

import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MatchApplicationResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MyApplicationsResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.ReplyApplicationRequest
import com.teamsparta.tikitaka.infra.security.UserPrincipal

interface MatchApplicationService {
    fun applyMatch(userId: Long, matchId: Long): MatchApplicationResponse

    fun replyMatchApplication(
        userId: Long,
        applicationId: Long,
        request: ReplyApplicationRequest
    ): MatchApplicationResponse


    fun getMyApplications(userId: Long): List<MyApplicationsResponse>

    fun deleteMatchApplication(
        principal: UserPrincipal, applicationId: Long
    )
}