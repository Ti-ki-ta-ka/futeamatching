package com.teamsparta.tikitaka.infra.oauth.kakako

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class KakaoOAuthLoginClient(
    @Value("\${oauth2.kakao.client-id}") val clientId: String,
    @Value("\${oauth2.kakao.client-secret}") val clientSecret: String,
    @Value("\${oauth2.kakao.redirect_url}") val redirectUrl: String,
    @Value("\${oauth2.kakao.auth_server_base_url}") val authServerBaseUrl: String,
    @Value("\${oauth2.kakao.resource_server_base_url}") val resourceServerBaseUrl: String,
    private val restClient: RestClient
) {
    fun generateLoginPageUrl(): String {
        return StringBuilder(authServerBaseUrl)
            .append("/oauth/authorize")
            .append("?client_id=").append(clientId)
            .append("&redirect_uri=").append(redirectUrl)
            .append("&response_type=").append("code")
            .toString()
    }

    fun getAccessToken(code: String): String {
        val requestData = mutableMapOf(
            "grant_type" to "authorization_code",
            "client_id" to clientId,
            "redirect_uri" to redirectUrl,
            "client_secret" to clientSecret,
            "code" to code
        )
        return restClient.post()
            .uri("$authServerBaseUrl/oauth/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(LinkedMultiValueMap<String, String>().apply { this.setAll(requestData) })
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, response ->
                throw RuntimeException("${response.statusCode} kakao AccessToken 조회 실패")
            }
            .body<KakaoTokenResponse>()
            ?.accessToken
            ?: throw RuntimeException("kakao AccessToken 조회 실패")
    }

    fun retrieveUserInfo(accessToken: String): KakaoOAuthUserInfo {
        return restClient.get()
            .uri("$resourceServerBaseUrl/v2/user/me")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .onStatus(HttpStatusCode::isError) { _, response ->
                throw RuntimeException("${response.statusCode} kakao user 조회 실패")
            }
            .body<KakaoOAuthUserInfo>()
            ?: throw RuntimeException("kakao user조회 실패")
    }
}