package com.teamsparta.tikitaka.domain.users.dto

import com.teamsparta.tikitaka.domain.users.model.Users

data class OAuthProviderResponse(
    val oAuthProvider: String?
){
    companion object {
        fun from(user: Users): OAuthProviderResponse {
            return OAuthProviderResponse(
                oAuthProvider = user.oAuthProvider
            )
        }
    }
}
