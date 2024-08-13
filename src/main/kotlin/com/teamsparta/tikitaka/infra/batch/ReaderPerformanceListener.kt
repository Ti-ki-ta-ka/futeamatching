package com.teamsparta.tikitaka.infra.batch

import com.teamsparta.tikitaka.domain.evaluation.model.Evaluation
import org.slf4j.LoggerFactory
import org.springframework.batch.core.ItemReadListener

class ReaderPerformanceListener : ItemReadListener<Evaluation> {
    private val log = LoggerFactory.getLogger(ReaderPerformanceListener::class.java)
    private var startTime: Long = 0

    override fun beforeRead() {
        startTime = System.currentTimeMillis()
    }

    override fun afterRead(item: Evaluation) {
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        log.info("ItemReader execution time for item: ${duration}ms")
    }

    override fun onReadError(ex: Exception) {
        log.error("Error during reading item", ex)
    }
}
