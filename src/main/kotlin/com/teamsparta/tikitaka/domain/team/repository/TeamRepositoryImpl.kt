package com.teamsparta.tikitaka.domain.team.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathBuilder
import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.team.model.QTeam
import com.teamsparta.tikitaka.domain.team.model.Team
import com.teamsparta.tikitaka.infra.querydsl.QueryDslSupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class TeamRepositoryImpl : CustomTeamRepository, QueryDslSupport() {

    private val team = QTeam.team

    override fun findAllByPageable(
        pageable: Pageable,
        region: String?
    ): Page<Team> {
        val whereClause = BooleanBuilder()

        if (region != null) {
            val regionEnum = try {
                Region.fromString(region)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid region: $region")
            }
            whereClause.and(team.region.eq(regionEnum))
        }

        val totalCount = queryFactory.select(team.count())
            .from(team)
            .where(whereClause)
            .fetchOne() ?: 0L

        val contents = queryFactory.selectFrom(team)
            .where(whereClause)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable, team))
            .fetch()

        return PageImpl(contents, pageable, totalCount)
    }

    override fun findByName(pageable: Pageable, name: String, region: String?): Page<Team> {
        val whereClause = BooleanBuilder()
        whereClause.and(team.name.containsIgnoreCase(name))

        if (region != null) {
            val regionEnum = try {
                Region.fromString(region)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid region: $region")
            }
            whereClause.and(team.region.eq(regionEnum))
        }

        val totalCount = queryFactory.select(team.count())
            .from(team)
            .where(whereClause)
            .fetchOne() ?: 0L

        val contents = queryFactory.selectFrom(team)
            .where(whereClause)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*getOrderSpecifier(pageable, team))
            .fetch()

        return PageImpl(contents, pageable, totalCount)
    }


    private fun getOrderSpecifier(
        pageable: Pageable,
        path: EntityPathBase<*>
    ): Array<OrderSpecifier<*>> {
        val pathBuilder = PathBuilder(path.type, path.metadata)

        return pageable.sort.toList().map { order ->
            val sortOrder = if (order.isAscending) Order.ASC else Order.DESC
            val expression = when (order.property) {
                "created_at" -> QTeam.team.createdAt
                "manner_score" -> QTeam.team.mannerScore
                else -> pathBuilder.get(order.property)
            }
            OrderSpecifier(
                sortOrder, expression as Expression<Comparable<*>>
            )
        }.toTypedArray()
    }
}
