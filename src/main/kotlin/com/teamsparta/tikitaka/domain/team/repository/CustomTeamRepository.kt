package com.teamsparta.tikitaka.domain.team.repository

import com.teamsparta.tikitaka.domain.team.model.Team
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomTeamRepository {
    fun findAllByPageable(pageable: Pageable, region: String?): Page<Team>
    fun findTeamsByName(pageable: Pageable, name: String, region: String?): Page<Team>

}