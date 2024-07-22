@file:JvmName("MatchApplicationRepositoryKt")

package com.teamsparta.tikitaka.domain.matchApplication.repository

import com.teamsparta.tikitaka.domain.match.model.QMatch
import com.teamsparta.tikitaka.domain.matchApplication.model.MatchApplication
import com.teamsparta.tikitaka.domain.matchApplication.model.QMatchApplication
import com.teamsparta.tikitaka.infra.querydsl.QueryDslSupport
import java.time.LocalDate

class MatchApplicationRepositoryImpl : CustomMatchApplicationRepository, QueryDslSupport() {

    private val qMatchApplication = QMatchApplication.matchApplication
    private val qMatch = QMatch.match

    override fun findByTeamIdAndMatchDate(teamId: Long, matchDate: LocalDate): List<MatchApplication> {

        return queryFactory.selectFrom(qMatchApplication)
            .join(qMatchApplication.matchPost, qMatch)
            .where(
                qMatchApplication.applyTeamId.eq(teamId)
                    .and(qMatch.matchDate.year().eq(matchDate.year))
                    .and(qMatch.matchDate.month().eq(matchDate.monthValue))
                    .and(qMatch.matchDate.dayOfMonth().eq(matchDate.dayOfMonth))
            )
            .fetch()
    }
}
