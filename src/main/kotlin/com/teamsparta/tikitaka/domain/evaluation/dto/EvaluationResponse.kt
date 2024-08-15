package com.teamsparta.tikitaka.domain.evaluation.dto

import com.teamsparta.tikitaka.domain.evaluation.model.Evaluation

data class EvaluationResponse(
    val evaluationStatus: Boolean
) {
    companion object {
        fun from(evaluationResponse: Evaluation) = EvaluationResponse(
            evaluationStatus = evaluationResponse.evaluationStatus
        )
    }
}
