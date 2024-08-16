package com.teamsparta.tikitaka.domain.team.repository.teammember

import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TeamMemberRepository : JpaRepository<TeamMember, Long> {
    fun findByUserId(userId: Long): TeamMember

    @Query("SELECT t FROM TeamMember t WHERE t.userId = :userId")
    fun findByUserIdOrNull(userId: Long): TeamMember?

    fun findAllByTeamId(teamId: Long): List<TeamMember>

    fun findByTeamId(teamId: Long): List<TeamMember>?
    fun findByUserIdAndTeamId(userId: Long, teamId: Long): TeamMember?
}