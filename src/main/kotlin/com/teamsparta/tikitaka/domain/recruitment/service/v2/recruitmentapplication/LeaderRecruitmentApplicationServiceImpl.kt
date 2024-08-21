package com.teamsparta.tikitaka.domain.recruitment.service.v2.recruitmentapplication

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.UpdateApplicationResponseStatus
import com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication.ResponseStatus
import com.teamsparta.tikitaka.domain.recruitment.repository.RecruitmentRepository
import com.teamsparta.tikitaka.domain.recruitment.repository.recruitmentapplication.RecruitmentApplicationRepository
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import com.teamsparta.tikitaka.domain.team.service.v2.LeaderTeamService2
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LeaderRecruitmentApplicationServiceImpl(
    private val recruitmentRepository: RecruitmentRepository,
    private val recruitmentApplicationRepository: RecruitmentApplicationRepository,
    private val teamRepository: TeamRepository,
    private val teamService: LeaderTeamService2,
    private val userRepository: UsersRepository
) : LeaderRecruitmentApplicationService {

    @Transactional
    override fun replyRecruitmentApplication(
        userId: Long, recruitmentId: Long, applicationId: Long, request: UpdateApplicationResponseStatus
    ): RecruitmentApplicationResponse {
        val recruitmentPost = recruitmentRepository.findByIdOrNull(recruitmentId) ?: throw ModelNotFoundException(
            "recruitment",
            recruitmentId
        )
        val user = userRepository.findByIdOrNull(userId) ?: throw ModelNotFoundException("user", userId)
        if (recruitmentPost.userId != userId) {
            throw AccessDeniedException("You can only respond to applications for your own team's Recruitment.")
        }
        if (recruitmentPost.closingStatus) {
            throw IllegalStateException("This recruitment is already closed.")
        }

        val application = recruitmentApplicationRepository.findByIdAndRecruitmentId(applicationId, recruitmentId)
            ?: throw ModelNotFoundException("recruitmentApplication", applicationId)
        if (application.responseStatus == ResponseStatus.CANCELLED) {
            throw IllegalStateException("This application is already cancelled.")
        }
        if (application.responseStatus == ResponseStatus.APPROVE || application.responseStatus == ResponseStatus.REJECT) {
            throw IllegalStateException("You cannot respond to an application that has already been resolved.")
        }

        val team = teamRepository.findByIdOrNull(recruitmentPost.teamId) ?: throw ModelNotFoundException(
            "team",
            recruitmentPost.teamId
        )
        if (team.countMember >= 50) {
            throw IllegalStateException("The team is full and cannot accept more members.")
        }

        if (request.responseStatus == ResponseStatus.CANCELLED.toString() && application.userId != userId) {
            throw AccessDeniedException("Only the applicant can cancel their application.")
        }

        if (request.responseStatus == ResponseStatus.APPROVE.toString()) {
            team.countMember += 1
            teamService.addMember(application.userId, recruitmentPost.teamId)
        }

        application.responseStatus = ResponseStatus.valueOf(request.responseStatus)
        if (application.responseStatus == ResponseStatus.APPROVE) {
            rejectOtherApplications(recruitmentId, applicationId)
        }
        return RecruitmentApplicationResponse.from(application, user)
    }

    private fun rejectOtherApplications(recruitmentId: Long, approvedApplicationId: Long) {
        val otherApplications =
            recruitmentApplicationRepository.findByRecruitmentIdAndResponseStatus(recruitmentId, ResponseStatus.WAITING)
        for (application in otherApplications) {
            if (application.id != approvedApplicationId) {
                application.responseStatus = ResponseStatus.REJECT
            }
        }
    }
}