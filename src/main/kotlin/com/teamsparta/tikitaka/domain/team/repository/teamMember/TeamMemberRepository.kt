package com.teamsparta.tikitaka.domain.team.repository.teamMember

import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import org.springframework.data.jpa.repository.JpaRepository

interface TeamMemberRepository : JpaRepository<TeamMember, Long> {
    fun findByUserId(userId: Long): TeamMember
    fun findByTeamId(teamId: Long): List<TeamMember>?
    fun findByUserIdAndTeamId(userId: Long, teamId: Long): TeamMember?
}