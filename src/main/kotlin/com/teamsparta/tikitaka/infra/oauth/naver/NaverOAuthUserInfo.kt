package com.teamsparta.tikitaka.infra.oauth.naver

import com.fasterxml.jackson.annotation.JsonProperty

data class NaverOAuthUserInfo(
    @JsonProperty("resultcode")
    val resultcode: String,
    val message: String,
    val response: NaverUserResponse
)
