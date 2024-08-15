package com.teamsparta.tikitaka.domain.evaluation.repository

import com.teamsparta.tikitaka.domain.evaluation.model.Evaluation
import org.springframework.data.jpa.repository.JpaRepository

interface EvaluationRepository : JpaRepository<Evaluation, Long>, CustomEvaluationRepository {
    fun existsByEvaluatorIdAndEvaluateeTeamId(evaluatorId: Long, evaluateeTeamId: Long): Boolean
}