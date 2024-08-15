package com.teamsparta.tikitaka.infra.oauth.naver

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverUserResponse(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("email")
    val email: String?
)
