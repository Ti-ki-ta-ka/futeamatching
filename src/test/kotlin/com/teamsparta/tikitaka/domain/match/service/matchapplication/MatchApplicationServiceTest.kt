package com.teamsparta.tikitaka.domain.match.service.matchapplication

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.ReplyApplicationRequest
import com.teamsparta.tikitaka.domain.match.model.Match
import com.teamsparta.tikitaka.domain.match.model.matchapplication.ApproveStatus
import com.teamsparta.tikitaka.domain.match.model.matchapplication.MatchApplication
import com.teamsparta.tikitaka.domain.match.repository.MatchRepository
import com.teamsparta.tikitaka.domain.match.repository.matchapplication.MatchApplicationRepository
import com.teamsparta.tikitaka.domain.match.service.v2.matchapplication2.MatchApplicationServiceImpl2
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.team.repository.teamMember.TeamMemberRepository
import com.teamsparta.tikitaka.domain.users.model.Users
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class MatchApplicationServiceTest : BehaviorSpec({

    val matchApplicationRepository = mockk<MatchApplicationRepository>()
    val matchRepository = mockk<MatchRepository>()
    val usersRepository = mockk<UsersRepository>()
    val teamMemberRepository = mockk<TeamMemberRepository>()

    val matchApplicationService = spyk(
        MatchApplicationServiceImpl2(
            matchApplicationRepository,
            matchRepository,
            usersRepository,
            teamMemberRepository
        ),
        recordPrivateCalls = true
    )

    beforeEach {
        clearAllMocks()
    }

    context("MatchApplicationService.replyMatchApplication()") {
        given("매치를 신청") {
            `when`("신청의 ResponseStatus가 WAITING이며, 해당 신청의 승인할 경우") {
                then("ResponseStatus는 APPROVE로 변경") {
                    val testUser = mockk<Users> {
                        every { id } returns 1L
                    }
                    val testMatchPost = Match(
                        title = "title",
                        matchDate = LocalDateTime.of(2024, 1, 1, 1, 1),
                        location = "location",
                        region = Region.fromString("SEOUL"),
                        content = "content",
                        matchStatus = false,
                        teamId = 1L,
                        userId = 1L
                    )

                    val matchApplication = MatchApplication(
                        applyUserId = 2L,
                        applyTeamId = 2L,
                        matchPost = testMatchPost,
                        approveStatus = ApproveStatus.WAITING,
                        createdAt = LocalDateTime.of(2024, 1, 1, 0, 0)
                    ).also { it.id = 1L }

                    val userTeamMember = mockk<TeamMember> {
                        every { teamRole } returns TeamRole.LEADER
                    }

                    val request = ReplyApplicationRequest(ApproveStatus.APPROVE.toString())

                    every { usersRepository.findByIdOrNull(1L) } returns testUser
                    every { matchRepository.findByIdOrNull(1L) } returns testMatchPost
                    every { matchApplicationRepository.findByIdOrNull(1L) } returns matchApplication
                    every {
                        teamMemberRepository.findByUserIdAndTeamId(1L, 1L)
                    } returns userTeamMember
                    every {
                        matchApplicationRepository.findByMatchPostIdAndApproveStatus(
                            1L,
                            ApproveStatus.WAITING
                        )
                    } returns emptyList()

                    every { matchApplicationRepository.save(any()) } returns matchApplication

                    val response =
                        matchApplicationService.replyMatchApplication(
                            1L,
                            1L,
                            1L,
                            request
                        )

                    response.approveStatus shouldBe "APPROVE"
                }
            }
        }
    }
})
