package com.teamsparta.tikitaka.infra.batch.config

import com.teamsparta.tikitaka.domain.team.model.Team
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class ClearTeamScoresStepConfig(
    private val teamRepository: TeamRepository,
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {

    @Bean
    fun clearTeamScoresStep(): Step {
        return StepBuilder("clearTeamScoresStep", jobRepository).chunk<Team, Team>(100, transactionManager)
            .reader(clearTeamScoresItemReader()).processor(clearTeamScoresItemProcessor())
            .writer(clearTeamScoresItemWriter()).build()
    }

    @StepScope
    @Bean
    fun clearTeamScoresItemReader(): ItemReader<Team> {
        val teams = teamRepository.findAll().iterator()
        return ItemReader {
            if (teams.hasNext()) {
                teams.next()
            } else {
                null
            }
        }
    }

    @StepScope
    @Bean
    fun clearTeamScoresItemProcessor(): ItemProcessor<Team, Team> {
        return ItemProcessor { team ->
            team.mannerScore = 0
            team.tierScore = 0
            team.attendanceScore = 0
            team
        }
    }

    @StepScope
    @Bean
    fun clearTeamScoresItemWriter(): ItemWriter<Team> {
        return ItemWriter { teams ->
            teamRepository.saveAll(teams)
        }
    }
}