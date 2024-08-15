package com.teamsparta.tikitaka.domain.recruitment.service.v2.recruitmentapplication

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication.RecruitmentApplication
import com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication.ResponseStatus
import com.teamsparta.tikitaka.domain.recruitment.repository.RecruitmentRepository
import com.teamsparta.tikitaka.domain.recruitment.repository.recruitmentapplication.RecruitmentApplicationRepository
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RecruitmentApplicationServiceImpl(
    private val recruitmentRepository: RecruitmentRepository,
    private val recruitmentApplicationRepository: RecruitmentApplicationRepository,
    private val userRepository: UsersRepository
) : RecruitmentApplicationService {
    @Transactional
    override fun applyRecruitment(
        userId: Long, recruitmentId: Long
    ): RecruitmentApplicationResponse {
        val applicant = userRepository.findByIdOrNull(userId) ?: throw ModelNotFoundException("user", userId)
        if (applicant.teamStatus) throw IllegalStateException("you are already affiliated with another team.")

        val recruitment = recruitmentRepository.findByIdOrNull(recruitmentId) ?: throw ModelNotFoundException(
            "recruitment",
            recruitmentId
        )
        if (recruitment.closingStatus) throw IllegalStateException("This recruitment has already been closed.")

        val newApplication = RecruitmentApplication.of(recruitment, userId, "WAITING")
        return RecruitmentApplicationResponse.from(recruitmentApplicationRepository.save(newApplication))
    }

    @Transactional
    override fun cancelApplication(
        principal: UserPrincipal, recruitmentId: Long, applicationId: Long
    ): RecruitmentApplicationResponse {
        val application = findApplicationById(applicationId)
        if (application.userId != principal.id) {
            throw AccessDeniedException(" You do not have permission to cancel.")
        }
        validateCancelable(application)
        application.responseStatus = ResponseStatus.CANCELLED

        return RecruitmentApplicationResponse.from(application)
    }

    override fun getMyApplications(
        principal: UserPrincipal
    ): List<RecruitmentApplicationResponse> {
        val applications = recruitmentApplicationRepository.findByUserId(principal.id)
            ?: throw ModelNotFoundException("applications by userId", principal.id)

        return applications.map { RecruitmentApplicationResponse.from(it) }
    }

    private fun findApplicationById(applicationId: Long) =
        recruitmentApplicationRepository.findByIdOrNull(applicationId) ?: throw ModelNotFoundException(
            "recruitmentApplication",
            applicationId
        )

    private fun validateCancelable(application: RecruitmentApplication) {
        when (application.responseStatus) {
            ResponseStatus.REJECT, ResponseStatus.APPROVE -> throw IllegalStateException("You cannot cancel an application that has already been approved or rejected.")
            ResponseStatus.CANCELLED -> throw IllegalStateException("You already canceled this application.")
            else -> {}
        }
    }


}