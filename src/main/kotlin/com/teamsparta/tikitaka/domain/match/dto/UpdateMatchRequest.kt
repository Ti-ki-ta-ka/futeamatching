package com.teamsparta.tikitaka.domain.match.dto

import java.time.LocalDateTime

class UpdateMatchRequest(
    val title: String,
    val matchDate: LocalDateTime,
    val location: String,
    val content: String,
    val matchStatus: Boolean,
)