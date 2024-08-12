package com.teamsparta.tikitaka.infra.oauth.oauthlogin.service

import com.teamsparta.tikitaka.domain.users.model.Users
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.oauth.kakako.KakaoOAuthUserInfo
import com.teamsparta.tikitaka.infra.oauth.naver.NaverOAuthUserInfo
import org.springframework.stereotype.Service

@Service
class SocialMemberDomainService(
    private val usersRepository: UsersRepository
) {
    fun registerIfAbsentKakao(userInfo: KakaoOAuthUserInfo): Users {
        return usersRepository.findByOAuthProviderId(userInfo.id.toString()) ?: usersRepository.save(
            Users(
                oAuthProvider = "KAKAO",
                name = userInfo.kakaoAccount.profile.nickname,
                email = userInfo.kakaoAccount.accountEmail,
                image = userInfo.kakaoAccount.profile.image,
                oAuthProviderId = userInfo.id.toString(),
                password = ""
            )
        )
    }

    fun registerIfAbsentNaver(userInfo: NaverOAuthUserInfo): Users {
        return usersRepository.findByOAuthProviderId(userInfo.response.id) ?: usersRepository.save(
            Users(
                oAuthProvider = "NAVER",
                name = userInfo.response.name,
                email = userInfo.response.email,
                image = "",
                oAuthProviderId = userInfo.response.id,
                password = ""
            )
        )
    }
}
