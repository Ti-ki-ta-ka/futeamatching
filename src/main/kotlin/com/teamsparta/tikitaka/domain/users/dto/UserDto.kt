package com.teamsparta.tikitaka.domain.users.dto

import com.teamsparta.tikitaka.domain.users.model.Users

data class UserDto(
    val userId: Long,
    val email: String,
    val name: String,
    val team_status: Boolean = false,
) {
    companion object {
        fun fromEntity(user: Users): UserDto {
            return UserDto(
                userId = user.id!!,
                name = user.name,
                email = user.email,
                team_status = user.teamStatus,
            )
        }
    }
}