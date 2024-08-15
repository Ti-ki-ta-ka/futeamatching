package com.teamsparta.tikitaka.domain.users.dto

data class SignUpRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String
)