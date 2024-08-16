package com.teamsparta.tikitaka.domain.recruitment.service.v2

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.recruitment.dto.PostRecruitmentRequest
import com.teamsparta.tikitaka.domain.recruitment.dto.RecruitmentResponse
import com.teamsparta.tikitaka.domain.recruitment.dto.UpdateRecruitmentRequest
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.domain.recruitment.model.Recruitment
import com.teamsparta.tikitaka.domain.recruitment.repository.RecruitmentRepository
import com.teamsparta.tikitaka.domain.recruitment.repository.recruitmentapplication.RecruitmentApplicationRepository
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import com.teamsparta.tikitaka.domain.team.repository.teammember.TeamMemberRepository
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LeaderRecruitmentServiceImpl(
    private val teamRepository: TeamRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val recruitmentRepository: RecruitmentRepository,
    private val recruitmentApplicationRepository: RecruitmentApplicationRepository,
) : LeaderRecruitmentService {
    override fun postRecruitment(principal: UserPrincipal, request: PostRecruitmentRequest): RecruitmentResponse {


        val leader = teamMemberRepository.findByUserId(principal.id)

        if (leader.teamRole != TeamRole.LEADER) {
            throw IllegalArgumentException("Only the current leader can reassign roles")
        }

        val recruitment = recruitmentRepository.save(
            Recruitment.of(
                teamId = leader.team.id!!,
                userId = principal.id,
                recruitType = request.recruitType,
                quantity = request.quantity,
                content = request.content,
                closingStatus = false,
            )
        )

        return RecruitmentResponse.from(recruitment)
    }

    @Transactional
    override fun updateRecruitmentPost(
        userId: Long,
        recruitmentId: Long,
        request: UpdateRecruitmentRequest
    ): RecruitmentResponse {
        val recruitmentPost = recruitmentRepository.findByIdOrNull(recruitmentId) ?: throw ModelNotFoundException(
            "recruitment",
            recruitmentId
        )

        if (recruitmentPost.userId != userId) {
            throw AccessDeniedException("You can only modify recruitment posted by your own team.")
        }
        if (recruitmentPost.closingStatus) {
            throw IllegalStateException("This recruitment is already closed.")
        }
        recruitmentPost.updateRecruitment(
            request
        )
        return RecruitmentResponse.from(recruitmentPost)
    }

    @Transactional
    override fun closeRecruitmentPost(userId: Long, recruitmentId: Long): RecruitmentResponse {
        val recruitmentPost = recruitmentRepository.findByIdOrNull(recruitmentId) ?: throw ModelNotFoundException(
            "recruitment",
            recruitmentId
        )
        if (recruitmentPost.userId != userId) {
            throw AccessDeniedException("You can only modify recruitment posted by your own team.")
        }
        if (recruitmentPost.closingStatus) {
            throw IllegalStateException("This recruitment is already closed.")
        }
        recruitmentPost.closingStatus = true
        return RecruitmentResponse.from(recruitmentPost)
    }

    @Transactional
    override fun deleteRecruitmentPost(userId: Long, recruitmentId: Long) {
        val recruitmentPost = recruitmentRepository.findByIdOrNull(recruitmentId) ?: throw ModelNotFoundException(
            "recruitment",
            recruitmentId
        )
        if (recruitmentPost.userId != userId) {
            throw AccessDeniedException("You can only modify recruitment posted by your own team.")
        }
        deleteRelatedApplications(recruitmentId)
        recruitmentPost.softDelete()
    }

    private fun deleteRelatedApplications(recruitmentId: Long) {
        val applications = recruitmentApplicationRepository.findByRecruitmentId(recruitmentId)
        applications?.forEach { it.delete() }
    }

    @Transactional
    override fun getRecruitmentApplications(
        userId: Long,
        recruitmentId: Long,
        pageable: Pageable,
        responseStatus: String?
    ): Page<RecruitmentApplicationResponse> {
        val recruitmentPost = recruitmentRepository.findByIdOrNull(recruitmentId) ?: throw ModelNotFoundException(
            "recruitment",
            recruitmentId
        )
        if (recruitmentPost.userId != userId) {
            throw AccessDeniedException("You can only view membership applications for your own team.")
        }
        val applications =
            recruitmentApplicationRepository.findApplicationsByRecruitmentId(pageable, recruitmentId, responseStatus)
        return applications
    }

    override fun getMyTeamRecruitments(
        principal: UserPrincipal,
        pageable: Pageable
    ): Page<RecruitmentResponse> {

        val leader = teamMemberRepository.findByUserId(principal.id)
        val team = teamRepository.findByIdOrNull(leader.team.id!!)
            ?: throw ModelNotFoundException("f", leader.team.id)

        if (leader.teamRole != TeamRole.LEADER) {
            throw AccessDeniedException(" only leader can access it.")
        }

        val recruitments = recruitmentRepository.findByTeamId(team.id!!, pageable)

        return recruitments.map { recruitment -> RecruitmentResponse.from(recruitment) }

    }
}
