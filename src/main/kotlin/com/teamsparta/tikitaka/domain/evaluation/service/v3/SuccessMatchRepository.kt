package com.teamsparta.tikitaka.domain.evaluation.service.v3

import com.teamsparta.tikitaka.domain.match.model.SuccessMatch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SuccessMatchRepository : JpaRepository<SuccessMatch, Long> {
    fun findByMatchDateBeforeAndEvaluationCreatedFalse(dateTime: LocalDateTime): List<SuccessMatch>
}