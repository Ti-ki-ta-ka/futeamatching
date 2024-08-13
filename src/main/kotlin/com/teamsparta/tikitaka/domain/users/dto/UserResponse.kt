package com.teamsparta.tikitaka.domain.users.dto

import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import java.time.LocalDateTime

data class UserResponse(
    val userId: Long,
    val email: String,
    val name : String,
    val role: TeamRole,
    val createdAt: LocalDateTime
)
