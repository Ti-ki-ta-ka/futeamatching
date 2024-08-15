package com.teamsparta.tikitaka.domain.recruitment.repository

import com.teamsparta.tikitaka.domain.recruitment.dto.RecruitmentResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomRecruitmentRepository {

    fun findRecruitmentsClosingStatusFalse(
        pageable: Pageable,
    ): Page<RecruitmentResponse>
}
