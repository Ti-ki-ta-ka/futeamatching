package com.teamsparta.tikitaka.domain.evaluation.repository

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.teamsparta.tikitaka.domain.evaluation.model.Evaluation
import java.time.LocalDateTime

interface CustomEvaluationRepository {
    fun softDeleteOldEvaluations(threshold: LocalDateTime, now: LocalDateTime)
    fun findEvaluationsWithPagination(queryFactory: JPAQueryFactory): JPAQuery<Evaluation>
}