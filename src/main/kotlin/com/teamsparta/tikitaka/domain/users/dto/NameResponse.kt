package com.teamsparta.tikitaka.domain.users.dto

import com.teamsparta.tikitaka.domain.users.model.Users

data class NameResponse(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(user: Users): NameResponse {
            return NameResponse(
                id = user.id!!,
                name = user.name
            )
        }
    }
}