package com.teamsparta.tikitaka.domain.users.dto

import com.teamsparta.tikitaka.domain.users.model.Users

data class PasswordResponse(
    val id: Long
) {
    companion object {
        fun from(user: Users): PasswordResponse {
            return PasswordResponse(
                id = user.id!!
            )
        }
    }
}
