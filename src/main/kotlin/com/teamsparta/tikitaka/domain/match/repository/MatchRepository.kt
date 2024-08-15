package com.teamsparta.tikitaka.domain.match.repository

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.match.model.Match
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface MatchRepository : JpaRepository<Match, Long>, CustomMatchRepository{

    @Query("SELECT m FROM Match m WHERE m.matchDate BETWEEN :startOfDay AND :endOfDay AND (:regions IS NULL OR m.region IN :regions)")
    fun findByDateAndRegions(
        @Param("startOfDay") startOfDay: LocalDateTime,
        @Param("endOfDay") endOfDay: LocalDateTime,
        @Param("regions") regions: List<Region>?,
        pageable: Pageable
    ): Page<Match>

}