package com.teamsparta.tikitaka.domain.team.repository

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.team.model.Team
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : JpaRepository<Team, Long>, CustomTeamRepository {
    fun findByRegionAndRankIsNotNull(region: Region, pageable: Pageable): Page<Team>
    fun findAllByRankIsNotNull(pageable: Pageable): Page<Team>
    fun findByName(name: String): Team?

}