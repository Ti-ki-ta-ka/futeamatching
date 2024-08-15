package com.teamsparta.tikitaka.domain.recruitment.repository.recruitmentapplication

import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomRecruitmentApplicationRepository {
    fun findApplicationsByRecruitmentId(
        pageable: Pageable,
        recruitmentId: Long,
        responseStatus: String?
    ): Page<RecruitmentApplicationResponse>
}