package com.teamsparta.tikitaka.domain.users.dto

data class LoginResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String
)
