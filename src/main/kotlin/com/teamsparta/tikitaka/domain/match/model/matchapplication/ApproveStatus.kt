package com.teamsparta.tikitaka.domain.match.model.matchapplication

enum class ApproveStatus {
    WAITING, APPROVE, REJECT, CANCELLED;

    companion object {
        fun fromString(approveStatus: String): ApproveStatus {
            return try {
                valueOf(approveStatus.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid category : $approveStatus")
            }
        }
    }
}