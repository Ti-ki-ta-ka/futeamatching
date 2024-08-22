package com.teamsparta.tikitaka.domain.recruitment.service.v2

import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.recruitment.dto.RecruitmentResponse
import com.teamsparta.tikitaka.domain.recruitment.repository.RecruitmentRepository
import com.teamsparta.tikitaka.domain.team.service.v3.TeamService3
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RecruitmentServiceImpl(
    private val recruitmentRepository: RecruitmentRepository,
    private val teamService: TeamService3
) : RecruitmentService {

    override fun getRecruitments(pageable: Pageable): Page<RecruitmentResponse> {
        val recruitments = recruitmentRepository.findRecruitmentsClosingStatusFalse(pageable)

        val recruitmentResponses = recruitments.map { recruitment ->
            val teamResponse = teamService.getTeam(recruitment.teamId)
            recruitment.copy(team = teamResponse)
        }

        return PageImpl(recruitmentResponses.toList(), pageable, recruitments.totalElements)
    }

    override fun getRecruitmentDetails(recruitmentId: Long): RecruitmentResponse {
        val recruitment = recruitmentRepository.findByIdOrNull(recruitmentId)
            ?: throw ModelNotFoundException("recruitment", recruitmentId)

        val teamResponse = teamService.getTeam(recruitment.teamId)
        return RecruitmentResponse.from(recruitment, teamResponse)
    }
}
