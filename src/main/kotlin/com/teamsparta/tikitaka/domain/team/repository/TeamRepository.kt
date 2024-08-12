package com.teamsparta.tikitaka.domain.team.repository

import com.teamsparta.tikitaka.domain.team.model.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : JpaRepository<Team, Long>, CustomTeamRepository