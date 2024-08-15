package com.teamsparta.tikitaka.domain.match.dto

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.match.model.Match
import java.time.LocalDateTime

data class MyTeamMatchResponse(

    val id: Long,
    val userId: Long,
    val title: String,
    val matchDate: LocalDateTime,
    val region: Region,
    val location: String,
    val content: String,
    val matchStatus: Boolean,
    val createdAt: LocalDateTime,

    ) {
    companion object {
        fun from(match: Match) = MyTeamMatchResponse(
            id = match.id!!,
            userId = match.userId,
            title = match.title,
            matchDate = match.matchDate,
            location = match.location,
            content = match.content,
            matchStatus = match.matchStatus,
            createdAt = match.createdAt,
            region = match.region,
        )
    }
}
