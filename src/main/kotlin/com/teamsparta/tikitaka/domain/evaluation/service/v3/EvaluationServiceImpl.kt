package com.teamsparta.tikitaka.domain.evaluation.service.v3

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.evaluation.dto.EvaluationRequest
import com.teamsparta.tikitaka.domain.evaluation.dto.EvaluationResponse
import com.teamsparta.tikitaka.domain.evaluation.model.Evaluation
import com.teamsparta.tikitaka.domain.evaluation.repository.EvaluationRepository
import com.teamsparta.tikitaka.domain.match.model.SuccessMatch
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EvaluationServiceImpl(
    private val evaluationRepository: EvaluationRepository,
    private val usersRepository: UsersRepository,
    private val successMatchRepository: SuccessMatchRepository,
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

    @Transactional
    override fun createEvaluationsForMatch(match: SuccessMatch) {
        val hostTeamEvaluation = Evaluation(
            evaluatorTeamId = match.hostTeamId,
            evaluateeTeamId = match.guestTeamId,
            evaluatorId = match.guestId,
            createdAt = LocalDateTime.now(),
        )
        val guestTeamEvaluation = Evaluation(
            evaluatorTeamId = match.guestTeamId,
            evaluateeTeamId = match.hostTeamId,
            evaluatorId = match.hostId,
            createdAt = LocalDateTime.now(),
        )

        evaluationRepository.save(hostTeamEvaluation)
        evaluationRepository.save(guestTeamEvaluation)

        match.evaluationCreatedTrue()
        successMatchRepository.save(match)


    }

    @Transactional
    override fun softDeleteOldEvaluations() {
        val now = LocalDateTime.now()
        val threshold = now.minusDays(90)
        evaluationRepository.softDeleteOldEvaluations(threshold, now)
    }
}