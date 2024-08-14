package com.teamsparta.tikitaka.infra.batch

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.teamsparta.tikitaka.domain.evaluation.model.QEvaluation
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.item.database.AbstractPagingItemReader
import org.springframework.util.CollectionUtils
import java.util.concurrent.CopyOnWriteArrayList

open class QuerydslZeroOffsetItemReader<T>(
    private val entityManagerFactory: EntityManagerFactory,
    val queryCreator: (JPAQueryFactory, Long?) -> JPAQuery<T>,
    private val idExtractor: (T) -> Long
) : AbstractPagingItemReader<T>() {

    private lateinit var entityManager: EntityManager
    private val jpaPropertyMap = mutableMapOf<String, Any>()
    private var lastId: Long? = null
    private val idPath = QEvaluation.evaluation.id
    var transacted = true

    override fun doOpen() {
        super.doOpen()
        entityManager = entityManagerFactory.createEntityManager(jpaPropertyMap)
    }

    override fun doReadPage() {
        clearIfTransacted()

        val query = createQuery().limit(pageSize.toLong())

        initResults()
        fetchQuery(query)
        updateLastId()
    }

    private fun clearIfTransacted() {
        if (transacted) {
            entityManager.clear()
        }
    }

    private fun createQuery(): JPAQuery<T> {
        val query = queryCreator(JPAQueryFactory(entityManager), lastId)
        lastId?.let {
            query.where(idPath.gt(it))
        }
        return query
    }

    private fun initResults() {
        if (CollectionUtils.isEmpty(results)) {
            results = CopyOnWriteArrayList()
        } else {
            results.clear()
        }
    }

    private fun fetchQuery(query: JPAQuery<T>) {
        if (!transacted) {
            val queryResult = query.fetch()
            for (entity in queryResult) {
                entityManager.detach(entity)
                results.add(entity)
            }
        } else {
            results.addAll(query.fetch())
        }
    }

    private fun updateLastId() {
        if (results.isNotEmpty()) {
            lastId = idExtractor(results.last())
        }
    }

    override fun doClose() {
        entityManager.close()
        super.doClose()
    }
}