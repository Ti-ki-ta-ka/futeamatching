package com.teamsparta.tikitaka.domain.evaluation.repository

import java.time.LocalDateTime

interface CustomEvaluationRepository {
    fun softDeleteOldEvaluations(threshold: LocalDateTime, now: LocalDateTime)
}