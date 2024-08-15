package com.teamsparta.tikitaka.domain.recruitment.service.v2

import com.teamsparta.tikitaka.domain.recruitment.dto.PostRecruitmentRequest
import com.teamsparta.tikitaka.domain.recruitment.dto.RecruitmentResponse
import com.teamsparta.tikitaka.domain.recruitment.dto.UpdateRecruitmentRequest
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LeaderRecruitmentService {
    fun postRecruitment(principal: UserPrincipal, request: PostRecruitmentRequest): RecruitmentResponse
    fun updateRecruitmentPost(userId: Long, recruitmentId: Long, request: UpdateRecruitmentRequest): RecruitmentResponse
    fun closeRecruitmentPost(userId: Long, recruitmentId: Long): RecruitmentResponse
    fun deleteRecruitmentPost(userId: Long, recruitmentId: Long)
    fun getRecruitmentApplications(
        userId: Long,
        recruitmentId: Long,
        pageable: Pageable,
        responseStatus: String?
    ): Page<RecruitmentApplicationResponse>

    fun getMyTeamRecruitments(
        principal: UserPrincipal,
        pageable: Pageable
    ): Page<RecruitmentResponse>
}
