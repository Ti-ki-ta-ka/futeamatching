package com.teamsparta.tikitaka.domain.users.service.v3

import com.teamsparta.tikitaka.domain.users.dto.LoginRequest
import com.teamsparta.tikitaka.domain.users.dto.LoginResponse
import com.teamsparta.tikitaka.domain.users.dto.NameRequest
import com.teamsparta.tikitaka.domain.users.dto.NameResponse
import com.teamsparta.tikitaka.domain.users.dto.PasswordRequest
import com.teamsparta.tikitaka.domain.users.dto.PasswordResponse
import com.teamsparta.tikitaka.domain.users.dto.SignUpRequest
import com.teamsparta.tikitaka.domain.users.dto.UserDto
import com.teamsparta.tikitaka.infra.security.UserPrincipal

interface UsersService3 {
    fun signUp(request: SignUpRequest): UserDto
    fun logIn(request: LoginRequest): LoginResponse
    fun logOut(token: String)
    fun validateRefreshTokenAndCreateToken(refreshToken: String): LoginResponse
    fun updateName(request: NameRequest, userPrincipal: UserPrincipal): NameResponse
    fun updatePassword(request: PasswordRequest, userPrincipal: UserPrincipal): PasswordResponse
}
