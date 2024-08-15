package com.teamsparta.tikitaka.domain.recruitment.service.v2.recruitmentapplication

import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.UpdateApplicationResponseStatus

interface LeaderRecruitmentApplicationService {
    fun replyRecruitmentApplication(
        userId: Long, recruitmentId: Long, applicationId: Long, request: UpdateApplicationResponseStatus
    ): RecruitmentApplicationResponse
}
