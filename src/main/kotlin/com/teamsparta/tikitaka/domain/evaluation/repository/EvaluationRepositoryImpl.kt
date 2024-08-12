package com.teamsparta.tikitaka.domain.evaluation.repository

import com.teamsparta.tikitaka.domain.evaluation.model.Evaluation
import com.teamsparta.tikitaka.domain.evaluation.model.QEvaluation
import com.teamsparta.tikitaka.infra.querydsl.QueryDslSupport
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class EvaluationRepositoryImpl : CustomEvaluationRepository, QueryDslSupport() {

    override fun findEvaluationsBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Evaluation> {
        val evaluation = QEvaluation.evaluation

        return queryFactory
            .selectFrom(evaluation)
            .where(
                evaluation.createdAt.between(startDate, endDate)
                    .and(evaluation.evaluationStatus.isTrue) // EvaluateStatus가 true인 조건 추가
            )
            .fetch()
    }

    override fun softDeleteOldEvaluations(threshold: LocalDateTime, now: LocalDateTime) {
        val evaluation = QEvaluation.evaluation
        queryFactory.update(evaluation)
            .set(evaluation.deletedAt, now)
            .where(
                evaluation.createdAt.lt(threshold)
                    .and(evaluation.deletedAt.isNull)
            )
            .execute()
    }
}