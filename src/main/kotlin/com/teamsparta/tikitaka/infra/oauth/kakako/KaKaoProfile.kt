package com.teamsparta.tikitaka.infra.oauth.kakako

import com.fasterxml.jackson.annotation.JsonProperty

data class KaKaoProfile(
    @JsonProperty("profile_image_url")
    val image: String?,
    @JsonProperty("nickname")
    val nickname: String
)
