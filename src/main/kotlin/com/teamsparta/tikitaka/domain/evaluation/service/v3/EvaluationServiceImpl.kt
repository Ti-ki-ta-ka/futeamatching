package com.teamsparta.tikitaka.domain.evaluation.service.v3

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.evaluation.dto.EvaluationRequest
import com.teamsparta.tikitaka.domain.evaluation.dto.EvaluationResponse
import com.teamsparta.tikitaka.domain.evaluation.model.Evaluation
import com.teamsparta.tikitaka.domain.evaluation.repository.EvaluationRepository
import com.teamsparta.tikitaka.domain.evaluation.repository.SuccessMatchRepository
import com.teamsparta.tikitaka.domain.match.model.SuccessMatch
import com.teamsparta.tikitaka.domain.team.repository.TeamRepository
import com.teamsparta.tikitaka.domain.users.dto.EmailDto
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import jakarta.mail.MessagingException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EvaluationServiceImpl(
    private val evaluationRepository: EvaluationRepository,
    private val usersRepository: UsersRepository,
    private val teamRepository: TeamRepository,
    private val successMatchRepository: SuccessMatchRepository,
    private val evaluationEmailService: EvaluationEmailService
) : EvaluationService {

    @Transactional
    override fun evaluate(
        evaluationId: Long,
        principal: UserPrincipal,
        request: EvaluationRequest
    ): EvaluationResponse {
        val user = usersRepository.findByIdOrNull(principal.id) ?: throw ModelNotFoundException("USER", principal.id)
        val evaluation = evaluationRepository.findByIdOrNull(evaluationId) ?: throw ModelNotFoundException(
            "EVALUATION",
            evaluationId
        )
        val evaluator = evaluation.evaluatorId
        if (user.id != evaluator) {
            throw AccessDeniedException("평가 권한이 없습니다")
        }
        if (evaluation.evaluationStatus) {
            throw IllegalArgumentException("이미 평가한 팀 입니다")
        }
        evaluation.updateEvaluation(request)
        evaluation.evaluationStatus = true
        return EvaluationResponse.from(evaluation)
    }

    private val emailMap = mutableMapOf<String, String>()

    @Transactional
    override fun createEvaluationsForMatch(match: SuccessMatch): EmailDto {
        val host = usersRepository.findByIdOrNull(match.hostId)
        val guest = usersRepository.findByIdOrNull(match.guestId)

        if (evaluationRepository.existsByEvaluatorIdAndEvaluateeTeamId(match.hostId, match.guestTeamId))
            throw IllegalArgumentException("이미 평가표가 생성 되었습니다")

        val hostTeamEvaluation = Evaluation(
            evaluatorTeamId = match.hostTeamId,
            evaluateeTeamId = match.guestTeamId,
            evaluatorId = match.hostId,
            createdAt = LocalDateTime.now()
        )
        val guestTeamEvaluation = Evaluation(
            evaluatorTeamId = match.guestTeamId,
            evaluateeTeamId = match.hostTeamId,
            evaluatorId = match.guestId,
            createdAt = LocalDateTime.now()
        )

        evaluationRepository.save(hostTeamEvaluation)
        evaluationRepository.save(guestTeamEvaluation)

        match.evaluationCreatedTrue()
        successMatchRepository.save(match)

        val message = evaluationEmailService.sendMessage()
        emailMap[host!!.email] = message
        emailMap[guest!!.email] = message

        try {
            evaluationEmailService.sendEmail(host.email, message)
            evaluationEmailService.sendEmail(guest.email, message)
        } catch (e: MessagingException) {
            throw IllegalArgumentException("메일 발송 중 오류가 발생했습니다.")
        }
        return EmailDto(message)
    }

    @Transactional
    override fun softDeleteOldEvaluations() {
        val now = LocalDateTime.now()
        val threshold = now.minusDays(90)
        evaluationRepository.softDeleteOldEvaluations(threshold, now)
    }
}