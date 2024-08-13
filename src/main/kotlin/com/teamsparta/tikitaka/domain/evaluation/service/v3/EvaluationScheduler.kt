package com.teamsparta.tikitaka.domain.evaluation.service.v3

import com.teamsparta.tikitaka.domain.evaluation.repository.SuccessMatchRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EvaluationScheduler(
    private val successMatchRepository: SuccessMatchRepository,
    private val evaluationService: EvaluationService,
) {

    @Scheduled(fixedRate = 600000)
    fun createEvaluations() {
        val now = LocalDateTime.now()
        val twoHoursAgo = now.minusHours(2)
        val matches = successMatchRepository.findByMatchDateBeforeAndEvaluationCreatedFalse(twoHoursAgo)

        matches.forEach { match ->
            evaluationService.createEvaluationsForMatch(match)
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    fun softDeleteOldEvaluations() {
        evaluationService.softDeleteOldEvaluations()
    }
}