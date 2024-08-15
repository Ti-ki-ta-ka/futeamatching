package com.teamsparta.tikitaka.domain.recruitment.repository

import com.teamsparta.tikitaka.domain.recruitment.model.Recruitment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecruitmentRepository : JpaRepository<Recruitment, Long>, CustomRecruitmentRepository {
    fun findByTeamId(teamId: Long, pageable: Pageable): Page<Recruitment>
}
