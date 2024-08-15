package com.teamsparta.tikitaka.infra.oauth.oauthlogin.service

import com.teamsparta.tikitaka.domain.team.repository.teamMember.TeamMemberRepository
import com.teamsparta.tikitaka.infra.oauth.kakako.KakaoOAuthLoginClient
import com.teamsparta.tikitaka.infra.oauth.naver.NaverOAuthLoginClient
import com.teamsparta.tikitaka.infra.security.jwt.JwtPlugin
import org.springframework.stereotype.Service

@Service
class OauthLoginService(
    private val kakaoOAuthLoginClient: KakaoOAuthLoginClient,
    private val naverOAuthLoginClient: NaverOAuthLoginClient,
    private val socialMemberDomainService: SocialMemberDomainService,
    private val jwtPlugin: JwtPlugin,
    private val teamMemberRepository: TeamMemberRepository
) {
    fun generateKakaoLoginPageUrl(): String {
        return kakaoOAuthLoginClient.generateLoginPageUrl()
    }

    fun kakaoLogin(code: String): String {
        val accessToken = kakaoOAuthLoginClient.getAccessToken(code)
        val userInfo = kakaoOAuthLoginClient.retrieveUserInfo(accessToken)
        val users = socialMemberDomainService.registerIfAbsentKakao(userInfo)
        return jwtPlugin.generateAccessToken(users.id.toString(), users.email)
    }

    fun generateNaverLoginPageUrl(): String {
        return naverOAuthLoginClient.generateLoginPageUrl()
    }

    fun naverLogin(code: String): String {
        val accessToken = naverOAuthLoginClient.getAccessToken(code)
        val userInfo = naverOAuthLoginClient.retrieveUserInfo(accessToken)
        val users = socialMemberDomainService.registerIfAbsentNaver(userInfo)
        return jwtPlugin.generateAccessToken(users.id.toString(), users.email)
    }
}