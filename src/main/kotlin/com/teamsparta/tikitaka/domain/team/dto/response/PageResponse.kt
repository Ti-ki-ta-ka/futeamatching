package com.teamsparta.tikitaka.domain.team.dto.response

import java.io.Serializable

data class PageResponse<T>(
    val content: List<T>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int
) : Serializable
