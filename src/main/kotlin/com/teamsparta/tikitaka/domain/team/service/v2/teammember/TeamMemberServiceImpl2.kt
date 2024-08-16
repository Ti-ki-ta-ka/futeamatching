package com.teamsparta.tikitaka.domain.team.service.v2.teammember

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.team.dto.response.RemoveMemberResopnse
import com.teamsparta.tikitaka.domain.team.dto.response.TeamMemberResponse
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.team.repository.teammember.TeamMemberRepository
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamMemberServiceImpl2(
    private val teamMemberRepository: TeamMemberRepository,
    private val userRepository: UsersRepository
) : TeamMemberService2 {

    override fun getMyTeamMembers(userId: Long): List<TeamMemberResponse> {
        val user = teamMemberRepository.findByUserId(userId)

        val myTeamMembers = teamMemberRepository.findByTeamId(user.team.id!!)
            ?: throw ModelNotFoundException("myTeamMembers", user.team.id)

        return myTeamMembers.map { TeamMemberResponse.of(it) }
    }

    @Transactional
    override fun leaveTeam(userId: Long, teamMemberId: Long): RemoveMemberResopnse {

        val teamMember = teamMemberRepository.findByIdOrNull(teamMemberId) ?: throw ModelNotFoundException(
            "teamMember",
            teamMemberId
        )
        if (teamMember.userId != userId) {
            throw AccessDeniedException("The provided TeamMemberId does not belong to the user.")
        }

        if (teamMember.teamRole == TeamRole.LEADER) {
            throw IllegalStateException("Leaders cannot leave the team without transferring leadership.")
        }
        val team = teamMember.team
        team.countMember -= 1

        val user = userRepository.findByIdOrNull(userId) ?: throw ModelNotFoundException("User", userId)
        user.teamStatus = false

        teamMember.softDelete()

        return RemoveMemberResopnse.from(teamMember)
    }
}

