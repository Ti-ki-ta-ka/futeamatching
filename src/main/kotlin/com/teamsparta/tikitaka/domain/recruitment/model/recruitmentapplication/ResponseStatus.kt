package com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication

enum class ResponseStatus {
    WAITING, APPROVE, REJECT, CANCELLED;

    companion object {
        fun fromString(approveStatus: String): ResponseStatus {
            return try {
                valueOf(approveStatus.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid category : $approveStatus")
            }
        }
    }
}