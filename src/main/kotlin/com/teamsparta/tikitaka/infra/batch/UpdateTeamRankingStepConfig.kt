package com.teamsparta.tikitaka.infra.batch

import com.teamsparta.tikitaka.domain.common.Region
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
class UpdateTeamRankingStepConfig(
    private val teamRepository: TeamRepository,
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager
) {

    @Bean
    fun updateTeamRankingStep(): Step {
        return StepBuilder("updateTeamRankingStep", jobRepository).chunk<Team, Team>(100, transactionManager)
            .reader(teamRankingItemReader()).processor(teamRankingItemProcessor()).writer(teamRankingItemWriter())
            .build()
    }

    @StepScope
    @Bean
    fun teamRankingItemReader(): ItemReader<Team> {
        val teams = teamRepository.findAllByOrderByTierScoreDesc().iterator()
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
    fun teamRankingItemProcessor(): ItemProcessor<Team, Team> {
        return object : ItemProcessor<Team, Team> {
            private var currentRank = 1L
            private var previousScore: Int? = null
            private var previousRank = 1L
            private var index = 0L

            private val regionRanks = mutableMapOf<Region, RegionRanking>()

            override fun process(team: Team): Team {
                index++

                if (team.tierScore == 0) {
                    team.rank = null
                } else {
                    if (previousScore == team.tierScore) {
                        team.rank = previousRank
                    } else {
                        team.rank = currentRank
                        previousRank = currentRank
                    }
                    previousScore = team.tierScore
                    currentRank = index + 1
                }

                val regionRanking = regionRanks.computeIfAbsent(team.region) { RegionRanking() }
                regionRanking.processTeam(team)

                return team
            }

            inner class RegionRanking {
                private var regionRank = 1L
                private var regionPreviousScore: Int? = null
                private var regionPreviousRank = 1L
                private var regionIndex = 0L

                fun processTeam(team: Team) {
                    regionIndex++

                    if (team.tierScore == 0) {
                        team.regionRank = null
                    } else {
                        if (regionPreviousScore == team.tierScore) {
                            team.regionRank = regionPreviousRank
                        } else {
                            team.regionRank = regionRank
                            regionPreviousRank = regionRank
                        }
                        regionPreviousScore = team.tierScore
                        regionRank = regionIndex + 1
                    }
                }
            }
        }
    }

    @StepScope
    @Bean
    fun teamRankingItemWriter(): ItemWriter<Team> {
        return ItemWriter { teams ->
            teamRepository.saveAll(teams)
        }
    }
}
