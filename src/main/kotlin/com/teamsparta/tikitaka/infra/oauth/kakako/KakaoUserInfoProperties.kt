package com.teamsparta.tikitaka.infra.oauth.kakako

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoUserInfoProperties(
    @JsonProperty("email")
    val accountEmail: String?,
    val profile: KaKaoProfile
)
