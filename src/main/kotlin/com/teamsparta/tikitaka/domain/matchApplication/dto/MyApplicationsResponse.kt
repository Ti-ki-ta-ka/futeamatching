package com.teamsparta.tikitaka.domain.matchApplication.dto

import com.teamsparta.tikitaka.domain.match.model.Match
import com.teamsparta.tikitaka.domain.matchApplication.model.MatchApplication

data class MyApplicationsResponse(
    val id: Long,
    val applyUserId: Long,
    val matchPost: Match,
    val location: String,
    val approveStatus: String
) {
    companion object {
        fun from(matchApplication: MatchApplication) = MyApplicationsResponse(
            id = matchApplication.id!!,
            applyUserId = matchApplication.applyUserId,
            matchPost = matchApplication.matchPost,
            location = matchApplication.matchPost.location,
            approveStatus = matchApplication.approveStatus.name

        )
    }
}

