package com.teamsparta.tikitaka.domain.recruitment.service.v2

import com.teamsparta.tikitaka.domain.recruitment.dto.RecruitmentResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface RecruitmentService {
    fun getRecruitments(pageable: Pageable): Page<RecruitmentResponse>
    fun getRecruitmentDetails(recruitmentId: Long): RecruitmentResponse
}
