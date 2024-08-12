package com.teamsparta.tikitaka.domain.evaluation.service.v3

import com.teamsparta.tikitaka.domain.evaluation.dto.EvaluationRequest
import com.teamsparta.tikitaka.domain.evaluation.dto.EvaluationResponse
import com.teamsparta.tikitaka.domain.match.model.SuccessMatch
import com.teamsparta.tikitaka.domain.users.dto.EmailDto
import com.teamsparta.tikitaka.infra.security.UserPrincipal

interface EvaluationService {
    fun evaluate(evaluationId: Long, principal: UserPrincipal, request: EvaluationRequest): EvaluationResponse
    fun createEvaluationsForMatch(match: SuccessMatch): EmailDto
    fun softDeleteOldEvaluations()
}