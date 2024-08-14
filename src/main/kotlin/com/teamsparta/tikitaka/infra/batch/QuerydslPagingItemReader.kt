package com.teamsparta.tikitaka.infra.batch

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.item.database.AbstractPagingItemReader
import org.springframework.util.CollectionUtils
import java.util.concurrent.CopyOnWriteArrayList

open class QuerydslPagingItemReader<T>(
    private val entityManagerFactory: EntityManagerFactory, val queryCreator: (JPAQueryFactory) -> JPAQuery<T>
) : AbstractPagingItemReader<T>() {

    private lateinit var entityManager: EntityManager

    private val jpaPropertyMap = mutableMapOf<String, Any>()
    var transacted = true
    var pageOffset = true

    override fun doOpen() {
        super.doOpen()
        entityManager = entityManagerFactory.createEntityManager(jpaPropertyMap)
    }

    override fun doReadPage() {
        clearIfTransacted()

        val query = createQuery().offset((page * pageSize).toLong()).limit(pageSize.toLong())

        initResults()
        fetchQuery(query)

        if (results.isEmpty()) {
            results = null
        }
    }

    private fun clearIfTransacted() {
        if (transacted) {
            entityManager.clear()
        }
    }

    private fun createQuery(): JPAQuery<T> = queryCreator(JPAQueryFactory(entityManager))

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

    override fun doClose() {
        entityManager.close()
        super.doClose()
    }

    override fun getPageSize(): Int {
        return if (pageOffset) super.getPageSize() else 0
    }
}
