package com.teamsparta.tikitaka.domain.evaluation.controller.v3

import com.teamsparta.tikitaka.domain.evaluation.dto.EvaluationRequest
import com.teamsparta.tikitaka.domain.evaluation.dto.EvaluationResponse
import com.teamsparta.tikitaka.domain.evaluation.service.v3.EvaluationService
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v3/evaluations")
class EvaluationController(
    private val evaluationService: EvaluationService
) {

    @PutMapping("/{evaluation-id}")
    fun evaluate(
        @PathVariable(name = "evaluation-id") evaluationId: Long,
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody request: EvaluationRequest
    ): ResponseEntity<EvaluationResponse> {
        return ResponseEntity.ok(evaluationService.evaluate(evaluationId, principal, request))
    }
}