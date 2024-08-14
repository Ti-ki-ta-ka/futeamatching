package com.teamsparta.tikitaka.infra.batch.listener

import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener

class StepPerformanceListener : StepExecutionListener {

    private val log = LoggerFactory.getLogger(StepPerformanceListener::class.java)
    private var startTime: Long = 0

    override fun beforeStep(stepExecution: StepExecution) {
        startTime = System.currentTimeMillis()
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        log.info("Step execution time: ${duration}ms")
        return stepExecution.exitStatus
    }
}
