package com.teamsparta.tikitaka.domain.match.service.v1.matchapplication

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.common.exception.TeamAlreadyAppliedException
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MatchApplicationResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MyApplicationsResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.ReplyApplicationRequest
import com.teamsparta.tikitaka.domain.match.model.matchapplication.ApproveStatus
import com.teamsparta.tikitaka.domain.match.model.matchapplication.MatchApplication
import com.teamsparta.tikitaka.domain.match.repository.MatchRepository
import com.teamsparta.tikitaka.domain.match.repository.matchapplication.MatchApplicationRepository
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.team.repository.teammember.TeamMemberRepository
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
    override fun applyMatch(userId: Long, matchId: Long): MatchApplicationResponse {
        usersRepository.findByIdOrNull(userId) ?: throw ModelNotFoundException("User", userId)

        val matchPost = matchRepository.findByIdOrNull(matchId) ?: throw ModelNotFoundException("match", matchId)

        val teamMember = teamMemberRepository.findByUserId(userId)
        val teamId = teamMember.team.id

        val matchDate = matchPost.matchDate.toLocalDate()

        val existingApplications = matchApplicationRepository.findByTeamIdAndMatchDate(teamId!!, matchDate)
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

    override fun getMyApplications(userId: Long): List<MyApplicationsResponse> {

        val teamMember = teamMemberRepository.findByUserId(userId)
        val teamId = teamMember.team.id
        return matchApplicationRepository.findByApplyTeamId(teamId!!)
            .map { application -> MyApplicationsResponse.from(application) }
    }
}
