package com.teamsparta.tikitaka.domain.users.dto

data class LoginRequest(
    val email: String,
    val password: String
)
