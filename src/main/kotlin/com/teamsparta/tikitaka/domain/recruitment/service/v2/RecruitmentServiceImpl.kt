package com.teamsparta.tikitaka.domain.recruitment.service.v2

import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.recruitment.dto.RecruitmentResponse
import com.teamsparta.tikitaka.domain.recruitment.repository.RecruitmentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RecruitmentServiceImpl(
    private val recruitmentRepository: RecruitmentRepository,
) : RecruitmentService {

    override fun getRecruitments(pageable: Pageable): Page<RecruitmentResponse> {
        return recruitmentRepository.findRecruitmentsClosingStatusFalse(pageable)
    }

    override fun getRecruitmentDetails(recruitmentId: Long): RecruitmentResponse {
        return recruitmentRepository.findByIdOrNull(recruitmentId)
            ?.let { recruitment -> RecruitmentResponse.from(recruitment) }
            ?: throw ModelNotFoundException("recruitment", recruitmentId)
    }


}
