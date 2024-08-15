package com.teamsparta.tikitaka.infra.oauth.kakako

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class KakaoOAuthUserInfo(
    val id: Long,
    val kakaoAccount: KakaoUserInfoProperties
)
