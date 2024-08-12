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
        val email = usersRepository.findByEmail(userInfo.kakaoAccount.accountEmail)
        if (email != null) throw IllegalArgumentException("이미 가입되어 있는 이메일입니다")
        val save = usersRepository.findByOAuthProviderId(userInfo.id.toString()) ?: usersRepository.save(
            Users(
                oAuthProvider = "KAKAO",
                name = userInfo.kakaoAccount.profile.nickname,
                email = userInfo.kakaoAccount.accountEmail,
                image = userInfo.kakaoAccount.profile.image,
                oAuthProviderId = userInfo.id.toString(),
                password = ""
            )

        )
        save.emailEnabled = true
        return usersRepository.save(save)
    }

    fun registerIfAbsentNaver(userInfo: NaverOAuthUserInfo): Users {
        val email = usersRepository.findByEmail(userInfo.response.email)
        if (email != null) throw IllegalArgumentException("이미 가입되어 있는 이메일입니다")
        val save = usersRepository.findByOAuthProviderId(userInfo.response.id) ?: usersRepository.save(
            Users(
                oAuthProvider = "NAVER",
                name = userInfo.response.name,
                email = userInfo.response.email,
                image = "",
                oAuthProviderId = userInfo.response.id,
                password = ""
            )
        )
        save.emailEnabled = true
        return usersRepository.save(save)
    }
}
