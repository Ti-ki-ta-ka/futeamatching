package com.teamsparta.tikitaka.domain.match.dto

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.match.model.Match
import java.io.Serializable
import java.time.LocalDateTime

data class MatchResponse(

    val id: Long,
    val teamId: Long,
    val userId: Long,
    val title: String,
    val matchDate: LocalDateTime,
    val region: Region,
    val location: String,
    val content: String,
    val matchStatus: Boolean,
    val createdAt: LocalDateTime,

    ) : Serializable {
    companion object {

        private const val serialVersionUID = 1L

        fun from(match: Match) = MatchResponse(
            id = match.id!!,
            teamId = match.teamId,
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
