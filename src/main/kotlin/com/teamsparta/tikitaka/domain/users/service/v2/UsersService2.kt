package com.teamsparta.tikitaka.domain.users.service.v2

import com.teamsparta.tikitaka.domain.users.dto.*
import com.teamsparta.tikitaka.infra.security.UserPrincipal

interface UsersService2 {
    fun signUp(request: SignUpRequest): UserDto
    fun logIn(request: LoginRequest): LoginResponse
    fun logOut(token: String)
    fun validateRefreshTokenAndCreateToken(refreshToken: String): LoginResponse
    fun updateName(request: NameRequest, userPrincipal: UserPrincipal): NameResponse
    fun updatePassword(request: PasswordRequest, userPrincipal: UserPrincipal): PasswordResponse
    fun getOAuthProvider(userPrincipal: UserPrincipal): OAuthProviderResponse?
    fun getMyProfile(userPrincipal: UserPrincipal): NameResponse
}