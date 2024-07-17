package com.teamsparta.tikitaka.domain.match.repository

import com.teamsparta.tikitaka.domain.match.model.Match
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository : JpaRepository<Match, Long> {
    fun findByDeletedAtIsNull(): List<Match>
}