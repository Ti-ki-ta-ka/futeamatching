package com.teamsparta.tikitaka.domain.recruitment.recruitmentapplication

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.UpdateApplicationResponseStatus
import com.teamsparta.tikitaka.domain.recruitment.model.RecruitType
import com.teamsparta.tikitaka.domain.recruitment.model.Recruitment
import com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication.RecruitmentApplication
import com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication.ResponseStatus
import com.teamsparta.tikitaka.domain.recruitment.repository.RecruitmentRepository
import com.teamsparta.tikitaka.domain.recruitment.repository.recruitmentapplication.RecruitmentApplicationRepository
import com.teamsparta.tikitaka.domain.recruitment.service.v2.recruitmentapplication.LeaderRecruitmentApplicationServiceImpl
import com.teamsparta.tikitaka.domain.team.model.Team
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamMember
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import com.teamsparta.tikitaka.domain.team.service.v2.LeaderTeamService
import com.teamsparta.tikitaka.domain.users.model.Users
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeaderRecruitmentApplicationServiceTest {
    private val recruitmentRepository = mockk<RecruitmentRepository>()
    private val recruitmentApplicationRepository = mockk<RecruitmentApplicationRepository>()
    private val teamRepository = mockk<TeamRepository>()
    private val userRepository = mockk<UsersRepository>()
    private val teamService = mockk<LeaderTeamService>()
    private val service = LeaderRecruitmentApplicationServiceImpl(
        recruitmentRepository,
        recruitmentApplicationRepository,
        teamRepository,
        teamService
    )

    @Test
    fun `모집 게시글이 존재하지 않을 때 ModelNotFoundException 발생`() {
        val userId = 1L
        val recruitmentId = 1L
        val applicationId = 1L
        val request = UpdateApplicationResponseStatus("APPROVE")

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns null

        shouldThrow<ModelNotFoundException> {
            service.replyRecruitmentApplication(userId, recruitmentId, applicationId, request)
        }
    }

    @Test
    fun `유저가 모집 게시글의 소유자가 아닐 때 AccessDeniedException 발생`() {
        val userId = 1L
        val recruitmentId = 1L
        val applicationId = 1L
        val request = UpdateApplicationResponseStatus("APPROVE")

        val recruitmentPost = Recruitment(
            teamId = 1L,
            userId = 2L,
            recruitType = RecruitType.TEAM_MEMBER,
            quantity = 5,
            content = "test content",
            closingStatus = false
        ).apply {
            id = recruitmentId
        }

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns recruitmentPost

        shouldThrow<AccessDeniedException> {
            service.replyRecruitmentApplication(userId, recruitmentId, applicationId, request)
        }
    }

    @Test
    fun `모집 게시글이 이미 닫혀 있을 때 IllegalStateException 발생`() {
        val userId = 1L
        val recruitmentId = 1L
        val applicationId = 1L
        val request = UpdateApplicationResponseStatus("APPROVE")

        val recruitmentPost = Recruitment(
            teamId = 1L,
            userId = 1L,
            recruitType = RecruitType.TEAM_MEMBER,
            quantity = 5,
            content = "test content",
            closingStatus = true
        ).apply {
            id = recruitmentId
        }

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns recruitmentPost

        shouldThrow<IllegalStateException> {
            service.replyRecruitmentApplication(userId, recruitmentId, applicationId, request)
        }
    }

    @Test
    fun `지원서가 존재하지 않을 때 ModelNotFoundException 발생`() {
        val userId = 1L
        val recruitmentId = 1L
        val applicationId = 1L
        val request = UpdateApplicationResponseStatus("APPROVE")

        val recruitmentPost = Recruitment(
            teamId = 1L,
            userId = 1L,
            recruitType = RecruitType.TEAM_MEMBER,
            quantity = 5,
            content = "test content",
            closingStatus = false
        ).apply {
            id = recruitmentId
        }

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns recruitmentPost
        every { recruitmentApplicationRepository.findByIdAndRecruitmentId(applicationId, recruitmentId) } returns null

        shouldThrow<ModelNotFoundException> {
            service.replyRecruitmentApplication(userId, recruitmentId, applicationId, request)
        }
    }

    @Test
    fun `지원서가 이미 취소된 경우 IllegalStateException 발생`() {
        val userId = 1L
        val recruitmentId = 1L
        val applicationId = 1L
        val request = UpdateApplicationResponseStatus("APPROVE")

        val recruitmentPost = Recruitment(
            teamId = 1L,
            userId = 1L,
            recruitType = RecruitType.TEAM_MEMBER,
            quantity = 5,
            content = "test content",
            closingStatus = false
        ).apply {
            id = recruitmentId
        }

        val application = mockk<RecruitmentApplication> {
            every { id } returns applicationId
            every { responseStatus } returns ResponseStatus.CANCELLED
        }

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns recruitmentPost
        every {
            recruitmentApplicationRepository.findByIdAndRecruitmentId(
                applicationId,
                recruitmentId
            )
        } returns application

        val exception = shouldThrow<IllegalStateException> {
            service.replyRecruitmentApplication(userId, recruitmentId, applicationId, request)
        }
        exception.message shouldBe "This application is already cancelled."

    }

    @Test
    fun `지원서가 이미 처리된 경우 IllegalStateException 발생`() {
        val userId = 1L
        val recruitmentId = 1L
        val applicationId = 1L
        val request = UpdateApplicationResponseStatus("APPROVE")

        val recruitmentPost = Recruitment(
            teamId = 1L,
            userId = 1L,
            recruitType = RecruitType.TEAM_MEMBER,
            quantity = 5,
            content = "test content",
            closingStatus = false
        ).apply {
            id = recruitmentId
        }

        val application = mockk<RecruitmentApplication> {
            every { id } returns applicationId
            every { responseStatus } returns ResponseStatus.APPROVE
        }

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns recruitmentPost
        every {
            recruitmentApplicationRepository.findByIdAndRecruitmentId(
                applicationId,
                recruitmentId
            )
        } returns application

        val exception = shouldThrow<IllegalStateException> {
            service.replyRecruitmentApplication(userId, recruitmentId, applicationId, request)
        }
        exception.message shouldBe "You cannot respond to an application that has already been resolved."
    }

    @Test
    fun `팀이 존재하지 않을 때 ModelNotFoundException 발생`() {
        val recruitmentId = 1L
        val applicationId = 1L
        val usersId = 1L
        val request = UpdateApplicationResponseStatus("APPROVE")

        val recruitmentPost = Recruitment(
            teamId = 1L,
            userId = 1L,
            recruitType = RecruitType.TEAM_MEMBER,
            quantity = 5,
            content = "test content",
            closingStatus = false
        ).apply {
            id = recruitmentId
        }

        val application = mockk<RecruitmentApplication> {
            every { id } returns applicationId
            every { responseStatus } returns ResponseStatus.WAITING
            every { userId } returns 2L
        }

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns recruitmentPost
        every {
            recruitmentApplicationRepository.findByIdAndRecruitmentId(
                applicationId,
                recruitmentId
            )
        } returns application
        every { teamRepository.findByIdOrNull(1L) } returns null

        shouldThrow<ModelNotFoundException> {
            service.replyRecruitmentApplication(usersId, recruitmentId, applicationId, request)
        }
    }

    @Test
    fun `팀이 가득 찬 경우 IllegalStateException 발생`() {
        val usersId = 1L
        val recruitmentId = 1L
        val applicationId = 1L
        val request = UpdateApplicationResponseStatus("APPROVE")

        val recruitmentPost = Recruitment(
            teamId = 1L,
            userId = 1L,
            recruitType = RecruitType.TEAM_MEMBER,
            quantity = 5,
            content = "test content",
            closingStatus = false
        ).apply {
            id = recruitmentId
        }


        val application = mockk<RecruitmentApplication> {
            every { id } returns applicationId
            every { responseStatus } returns ResponseStatus.WAITING
            every { userId } returns 2L
        }

        val team = mockk<Team> {
            every { id } returns 1L
            every { countMember } returns 50
        }

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns recruitmentPost
        every {
            recruitmentApplicationRepository.findByIdAndRecruitmentId(
                applicationId,
                recruitmentId
            )
        } returns application
        every { teamRepository.findByIdOrNull(1L) } returns team

        val exception = shouldThrow<IllegalStateException> {
            service.replyRecruitmentApplication(usersId, recruitmentId, applicationId, request)
        }

        exception.message shouldBe "The team is full and cannot accept more members."
    }

    @Test
    fun `지원서 취소 시 유저가 신청자가 아닌 경우 AccessDeniedException 발생`() {
        val usersId = 1L
        val recruitmentId = 1L
        val applicationId = 1L
        val request = UpdateApplicationResponseStatus("CANCELLED")

        val recruitmentPost = Recruitment(
            teamId = 1L,
            userId = 1L,
            recruitType = RecruitType.TEAM_MEMBER,
            quantity = 5,
            content = "test content",
            closingStatus = false
        ).apply {
            id = recruitmentId
        }

        val application = mockk<RecruitmentApplication> {
            every { id } returns applicationId
            every { responseStatus } returns ResponseStatus.WAITING
            every { userId } returns 2L
        }

        val team = mockk<Team> {
            every { id } returns 1L
            every { countMember } returns 10
        }

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns recruitmentPost
        every {
            recruitmentApplicationRepository.findByIdAndRecruitmentId(
                applicationId,
                recruitmentId
            )
        } returns application
        every { teamRepository.findByIdOrNull(1L) } returns team

        val exception = shouldThrow<AccessDeniedException> {
            service.replyRecruitmentApplication(usersId, recruitmentId, applicationId, request)
        }
        exception.message shouldBe "Only the applicant can cancel their application."
    }

    @Test
    fun `지원서를 승인하면 팀 멤버 수를 업데이트하고 다른 지원서를 거부 상태로 변경`() {
        val usersId = 1L
        val recruitmentId = 1L
        val applicationId = 1L
        val request = UpdateApplicationResponseStatus("APPROVE")

        val recruitmentPost = Recruitment(
            teamId = 1L,
            userId = usersId,
            recruitType = RecruitType.TEAM_MEMBER,
            quantity = 5,
            content = "Sample content",
            closingStatus = false
        ).apply {
            id = recruitmentId
        }

        val approvedApplication = RecruitmentApplication(
            userId = 2L,
            recruitment = recruitmentPost,
            responseStatus = ResponseStatus.WAITING,
            createdAt = LocalDateTime.now()
        ).apply {
            id = applicationId
        }

        val otherApplication1 = RecruitmentApplication(
            userId = 3L,
            recruitment = recruitmentPost,
            responseStatus = ResponseStatus.WAITING,
            createdAt = LocalDateTime.now()
        ).apply { id = 2L }

        val otherApplication2 = RecruitmentApplication(
            userId = 4L,
            recruitment = recruitmentPost,
            responseStatus = ResponseStatus.WAITING,
            createdAt = LocalDateTime.now()
        ).apply { id = 3L }

        val team = Team(
            name = "Test Team",
            userId = 1L,
            description = "test",
            countMember = 10,
            region = Region.JEJU
        ).apply { id = 1L }

        val user = Users(
            email = "test",
            password = "test_pw",
            name = "testUser",
        ).apply { id = 2L }

        every { recruitmentRepository.findByIdOrNull(recruitmentId) } returns recruitmentPost
        every {
            recruitmentApplicationRepository.findByIdAndRecruitmentId(
                applicationId,
                recruitmentId
            )
        } returns approvedApplication
        every {
            recruitmentApplicationRepository.findByRecruitmentIdAndResponseStatus(
                recruitmentId,
                ResponseStatus.WAITING
            )
        } returns listOf(approvedApplication, otherApplication1, otherApplication2)
        every { teamRepository.findByIdOrNull(1L) } returns team
        every { userRepository.findByIdOrNull(2L) } returns user
        every { teamService.addMember(2L, 1L) } answers {
            val newTeamMember = TeamMember(
                userId = 2L,
                team = team,
                createdAt = LocalDateTime.now()
            )
            newTeamMember.id = 1L
            newTeamMember
        }

        val response = service.replyRecruitmentApplication(usersId, recruitmentId, applicationId, request)

        response.responseStatus shouldBe ResponseStatus.APPROVE.toString()
        team.countMember shouldBe 11

        otherApplication1.responseStatus shouldBe ResponseStatus.REJECT
        otherApplication2.responseStatus shouldBe ResponseStatus.REJECT
    }

}