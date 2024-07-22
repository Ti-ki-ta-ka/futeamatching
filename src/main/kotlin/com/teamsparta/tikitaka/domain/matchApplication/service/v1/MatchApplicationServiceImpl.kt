package com.teamsparta.tikitaka.domain.matchApplication.service.v1

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.common.exception.TeamAlreadyAppliedException
import com.teamsparta.tikitaka.domain.match.repository.MatchRepository
import com.teamsparta.tikitaka.domain.matchApplication.dto.CreateApplicationRequest
import com.teamsparta.tikitaka.domain.matchApplication.dto.MatchApplicationResponse
import com.teamsparta.tikitaka.domain.matchApplication.dto.MyApplicationRequest
import com.teamsparta.tikitaka.domain.matchApplication.dto.MyApplicationsResponse
import com.teamsparta.tikitaka.domain.matchApplication.dto.ReplyApplicationRequest
import com.teamsparta.tikitaka.domain.matchApplication.model.ApproveStatus
import com.teamsparta.tikitaka.domain.matchApplication.model.MatchApplication
import com.teamsparta.tikitaka.domain.matchApplication.repository.MatchApplicationRepository
import com.teamsparta.tikitaka.domain.team.model.teamMember.TeamRole
import com.teamsparta.tikitaka.domain.team.repository.teamMember.TeamMemberRepository
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service

@Service
class MatchApplicationServiceImpl
    (
    private val matchApplicationRepository: MatchApplicationRepository,
    private val matchRepository: MatchRepository,
    private val usersRepository: UsersRepository,
    private val teamMemberRepository: TeamMemberRepository
) : MatchApplicationService {
    @Transactional
    override fun applyMatch(userId: Long, request: CreateApplicationRequest, matchId: Long): MatchApplicationResponse {
        usersRepository.findByIdOrNull(userId) ?: throw ModelNotFoundException("User", userId)

        val matchPost = matchRepository.findByIdOrNull(matchId) ?: throw ModelNotFoundException("match", matchId)
        val (teamId) = request

        val matchDate = matchPost.matchDate.toLocalDate()

        val existingApplications = matchApplicationRepository.findByTeamIdAndMatchDate(teamId, matchDate)
        if (existingApplications.any { it.approveStatus == ApproveStatus.WAITING || it.approveStatus == ApproveStatus.APPROVE }) {
            throw TeamAlreadyAppliedException("Your team already has a pending or approved application for the same match date.")
        }

        return matchApplicationRepository.save(MatchApplication.of(matchPost, teamId, userId))
            .let { MatchApplicationResponse.from(it) }
    }

    @Transactional
    override fun deleteMatchApplication(principal: UserPrincipal, applicationId: Long) {

        val matchApply = matchApplicationRepository.findByIdOrNull(applicationId) ?: throw ModelNotFoundException(
            "match",
            applicationId
        )
        if (matchApply.applyUserId != principal.id && !principal.authorities.contains(SimpleGrantedAuthority("ROLE_LEADER"))) throw AccessDeniedException(
            "You do not have permission to delete."
        )
        matchApply.delete()
        matchApply.approveStatus = ApproveStatus.CANCELLED

    }

    @Transactional
    override fun replyMatchApplication(
        userId: Long,
        applicationId: Long,
        request: ReplyApplicationRequest
    ): MatchApplicationResponse {
        usersRepository.findByIdOrNull(userId) ?: throw ModelNotFoundException("User", userId)
        val (approveStatus) = request
        val matchApply = matchApplicationRepository.findByIdOrNull(applicationId) ?: throw ModelNotFoundException(
            "MatchApplication",
            applicationId
        )

        if (matchApply.approveStatus == ApproveStatus.CANCELLED) {
            throw IllegalStateException("Cannot modify application with status CANCELLED")
        }

        val match = matchApply.matchPost
        val matchUserId = match.userId

        val userTeamMember = teamMemberRepository.findByUserIdAndTeamId(userId, match.teamId)
            ?: throw ModelNotFoundException("TeamMember", userId)

        if (userId != matchUserId) {
            if (userTeamMember.teamRole != TeamRole.LEADER) {
                throw AccessDeniedException("Only the author or the team leader can respond to this application")
            }
        }

        if (approveStatus == ApproveStatus.CANCELLED.toString()) {
            throw IllegalStateException("Only the applicant can cancel the match application.")
        }

        matchApply.approveStatus = ApproveStatus.fromString(approveStatus)
        return MatchApplicationResponse.from(matchApply)
    }

    override fun getMyApplications(
        request: MyApplicationRequest
    ): List<MyApplicationsResponse> {
        return matchApplicationRepository.findByApplyTeamId(request.teamId)
            .map { application -> MyApplicationsResponse.from(application) }
    }
}
