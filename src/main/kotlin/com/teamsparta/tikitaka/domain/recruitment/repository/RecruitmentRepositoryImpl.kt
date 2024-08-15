package com.teamsparta.tikitaka.domain.recruitment.repository

import com.teamsparta.tikitaka.domain.recruitment.dto.RecruitmentResponse
import com.teamsparta.tikitaka.domain.recruitment.model.QRecruitment
import com.teamsparta.tikitaka.infra.querydsl.QueryDslSupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class RecruitmentRepositoryImpl : CustomRecruitmentRepository, QueryDslSupport() {
    private val recruitment = QRecruitment.recruitment

    override fun findRecruitmentsClosingStatusFalse(
        pageable: Pageable,
    ): Page<RecruitmentResponse> {
        val totalCount =
            queryFactory.select(recruitment.count())
                .from(recruitment)
                .where(recruitment.closingStatus.isFalse)
                .fetchOne() ?: 0L

        val recruitments =
            queryFactory.selectFrom(recruitment)
                .where(recruitment.closingStatus.isFalse)
                .orderBy(recruitment.createdAt.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

        val recruitmentResponse = recruitments.map { rec ->
            RecruitmentResponse.from(rec)
        }

        return PageImpl(recruitmentResponse, pageable, totalCount)
    }

}
