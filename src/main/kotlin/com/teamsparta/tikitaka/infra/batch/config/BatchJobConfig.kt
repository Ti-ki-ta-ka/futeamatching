package com.teamsparta.tikitaka.infra.batch.config

import com.teamsparta.tikitaka.infra.batch.listener.StepPerformanceListener
import org.springframework.batch.core.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BatchJobConfig(
    private val clearTeamScoresStepConfig: ClearTeamScoresStepConfig,
    private val teamEvaluationStepConfig: TeamEvaluationStepConfig,
    private val updateTeamRankingStepConfig: UpdateTeamRankingStepConfig,
    private val jobRepository: JobRepository
) {

    @Bean
    fun teamEvaluationJob(): Job {
        return JobBuilder("teamEvaluationJob", jobRepository).start(clearTeamScoresStepConfig.clearTeamScoresStep())
            .next(teamEvaluationStepConfig.teamEvaluationStep(stepPerformanceListener()))
            .next(updateTeamRankingStepConfig.updateTeamRankingStep()).build()
    }

    @Bean
    fun stepPerformanceListener(): StepPerformanceListener {
        return StepPerformanceListener()
    }
}