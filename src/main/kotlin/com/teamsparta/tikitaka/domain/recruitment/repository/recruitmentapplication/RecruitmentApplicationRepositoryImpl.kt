package com.teamsparta.tikitaka.domain.recruitment.repository.recruitmentapplication

import com.querydsl.core.BooleanBuilder
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.domain.recruitment.model.QRecruitment
import com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication.QRecruitmentApplication
import com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication.ResponseStatus
import com.teamsparta.tikitaka.infra.querydsl.QueryDslSupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class RecruitmentApplicationRepositoryImpl : CustomRecruitmentApplicationRepository, QueryDslSupport() {
    private val recruitmentApplication = QRecruitmentApplication.recruitmentApplication
    private val recruitment = QRecruitment.recruitment

    override fun findApplicationsByRecruitmentId(
        pageable: Pageable, recruitmentId: Long, responseStatus: String?
    ): Page<RecruitmentApplicationResponse> {

        val whereClause = BooleanBuilder()
        responseStatus?.let { whereClause.and(recruitmentApplication.responseStatus.eq(ResponseStatus.fromString(it))) }
        recruitmentId.let { whereClause.and(recruitmentApplication.recruitment.id.eq(it)) }

        val totalCount =
            queryFactory.select(recruitmentApplication.count()).from(recruitmentApplication).where(whereClause)
                .fetchOne() ?: 0L

        val applications =
            queryFactory.selectFrom(recruitmentApplication).leftJoin(recruitmentApplication.recruitment, recruitment)
                .fetchJoin()
                .where(whereClause).orderBy(recruitmentApplication.createdAt.asc()).offset(pageable.offset)
                .limit(pageable.pageSize.toLong()).fetch()

        val recruitmentApplicationResponse = applications.map { application ->
            RecruitmentApplicationResponse(
                applicationId = application.id!!,
                userId = application.userId,
                responseStatus = application.responseStatus.toString(),
                createdAt = application.createdAt
            )
        }
        return PageImpl(recruitmentApplicationResponse, pageable, totalCount)
    }
}