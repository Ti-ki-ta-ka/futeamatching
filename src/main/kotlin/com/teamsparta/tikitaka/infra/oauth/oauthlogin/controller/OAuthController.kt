package com.teamsparta.tikitaka.infra.oauth.oauthlogin.controller

import com.teamsparta.tikitaka.infra.oauth.oauthlogin.service.OauthLoginService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.math.log

@RestController
@RequestMapping("/api/v2")
class OAuthController(
    private val oauthLoginService: OauthLoginService
) {

    @GetMapping("/oauth/kakako")
    fun kakaoLoginPage(response: HttpServletResponse) {
        val loginPageUrl = oauthLoginService.generateKakaoLoginPageUrl()
        response.sendRedirect(loginPageUrl)
    }

    @GetMapping("/login/oauth2/code/kakao")
    fun kakaoCallback(
        @RequestParam code: String
    ): ResponseEntity<String> {
        val accessToken = oauthLoginService.kakaoLogin(code)
        return ResponseEntity.ok(accessToken)
    }

    @GetMapping("/oauth/naver")
    fun naverLoginPage(response: HttpServletResponse) {
        val loginPageUrl = oauthLoginService.generateNaverLoginPageUrl()
        response.sendRedirect(loginPageUrl)
    }

    @GetMapping("/login/oauth2/code/naver")
    fun naverCallback(
        @RequestParam code: String
    ): ResponseEntity<String> {
        val accessToken = oauthLoginService.naverLogin(code)
        return ResponseEntity.ok(accessToken)
    }
}