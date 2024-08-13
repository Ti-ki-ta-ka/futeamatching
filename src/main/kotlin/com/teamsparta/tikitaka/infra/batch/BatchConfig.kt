package com.teamsparta.tikitaka.infra.batch

import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.evaluation.model.Evaluation
import com.teamsparta.tikitaka.domain.evaluation.repository.EvaluationRepository
import com.teamsparta.tikitaka.domain.team.model.Team
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchConfig(
    private val evaluationRepository: EvaluationRepository,
    private val teamRepository: TeamRepository,
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val entityManagerFactory: EntityManagerFactory
) {

    @Bean
    fun teamEvaluationJob(teamEvaluationStep: Step): Job {
        return JobBuilder("teamEvaluationJob", jobRepository).start(teamEvaluationStep).build()
    }

    @Bean
    fun teamEvaluationStep(
        performanceListener: ReaderPerformanceListener
    ): Step {
        return StepBuilder("teamEvaluationStep", jobRepository).chunk<Evaluation, Team>(100, transactionManager)
            .reader(evaluationItemReader(evaluationRepository)).processor(evaluationItemProcessor(teamRepository))
            .writer(teamItemWriter()).listener(performanceListener).build()
    }

    @StepScope
    @Bean
    fun evaluationItemReader(evaluationRepository: EvaluationRepository): QuerydslPagingItemReader<Evaluation> {
        return QuerydslPagingItemReader(entityManagerFactory = entityManagerFactory,
            queryCreator = { evaluationRepository.findEvaluationsWithPagination() }).apply {
            setPageSize(100)
        }
    }

    @StepScope
    @Bean
    fun evaluationItemProcessor(teamRepository: TeamRepository): ItemProcessor<Evaluation, Team> {
        return ItemProcessor { evaluation ->
            val teamId = evaluation.evaluateeTeamId
            val team = teamRepository.findById(teamId).orElseThrow { ModelNotFoundException("Team", teamId) }

            team.mannerScore += evaluation.mannerScore
            team.tierScore += evaluation.skillScore
            team.attendanceScore += evaluation.attendanceScore

            team
        }
    }

    @StepScope
    @Bean
    fun teamItemWriter(): ItemWriter<Team> {
        return ItemWriter { teams ->
            teams.forEach { team ->
                teamRepository.save(team)
            }
        }
    }

    @Bean
    fun readerPerformanceListener(): ReaderPerformanceListener {
        return ReaderPerformanceListener()
    }
}
