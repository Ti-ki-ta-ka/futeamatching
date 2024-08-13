package com.teamsparta.tikitaka.domain.evaluation.repository

import com.querydsl.jpa.impl.JPAQuery
import com.teamsparta.tikitaka.domain.evaluation.model.Evaluation
import com.teamsparta.tikitaka.domain.evaluation.model.QEvaluation
import com.teamsparta.tikitaka.infra.querydsl.QueryDslSupport
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class EvaluationRepositoryImpl : CustomEvaluationRepository, QueryDslSupport() {
    override fun softDeleteOldEvaluations(threshold: LocalDateTime, now: LocalDateTime) {
        val evaluation = QEvaluation.evaluation
        queryFactory.update(evaluation).set(evaluation.deletedAt, now).where(
            evaluation.createdAt.lt(threshold).and(evaluation.deletedAt.isNull)
        ).execute()
    }

    override fun findEvaluationsWithPagination(): JPAQuery<Evaluation> {
        return queryFactory.selectFrom(QEvaluation.evaluation)
            .where(QEvaluation.evaluation.evaluationStatus.isTrue)
            .orderBy(QEvaluation.evaluation.id.asc())
    }
}