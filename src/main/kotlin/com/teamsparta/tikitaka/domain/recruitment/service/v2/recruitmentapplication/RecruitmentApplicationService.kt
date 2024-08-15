package com.teamsparta.tikitaka.domain.recruitment.service.v2.recruitmentapplication

import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.infra.security.UserPrincipal

interface RecruitmentApplicationService {
    fun applyRecruitment(userId: Long, recruitmentId: Long): RecruitmentApplicationResponse
    fun cancelApplication(
        principal: UserPrincipal,
        recruitmentId: Long,
        applicationId: Long
    ): RecruitmentApplicationResponse

    fun getMyApplications(principal: UserPrincipal): List<RecruitmentApplicationResponse>
}