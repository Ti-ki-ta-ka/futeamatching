package com.teamsparta.tikitaka.domain.users.service.v1

import com.teamsparta.tikitaka.domain.common.exception.InvalidCredentialException
import com.teamsparta.tikitaka.domain.common.util.RedisUtils
import com.teamsparta.tikitaka.domain.users.dto.*
import com.teamsparta.tikitaka.domain.users.model.Users
import com.teamsparta.tikitaka.domain.users.model.toUpdateNameResponse
import com.teamsparta.tikitaka.domain.users.model.toUpdatePasswordResponse
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import com.teamsparta.tikitaka.infra.security.jwt.JwtPlugin
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin,
    private val redisUtils: RedisUtils
) : UsersService
{
    @Transactional
    override fun signUp(request: SignUpRequest): UserDto {
        if (usersRepository.findByEmail(request.email) != null) {
            throw InvalidCredentialException("중복된 이메일입니다.")
        }
        if (request.password != request.confirmPassword) {
            throw InvalidCredentialException("비밀번호 확인이 비밀번호와 일치하지 않습니다.")
        }
        val user = usersRepository.save(
            Users(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                name = request.name
            )
        )
        return UserDto.fromEntity(user)
    }

    override fun logIn(request: LoginRequest): LoginResponse {
        val user = usersRepository.findByEmail(request.email) ?: throw RuntimeException("email이 없습니다")
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw RuntimeException("비밀번호가 맞지 않습니다")
        }
        val accessToken = jwtPlugin.generateAccessToken(
            subject = user.id.toString(),
            email = user.email
        )
        val refreshToken = jwtPlugin.generateRefreshToken(
            subject = user.id.toString(),
            email = user.email
        )
        redisUtils.saveRefreshToken(refreshToken)
        return LoginResponse(accessToken = accessToken, refreshToken = refreshToken)
    }

    override fun logOut(token: String) {
        redisUtils.setDataExpire(token, "blacklisted")
    }

    override fun validateRefreshTokenAndCreateToken(refreshToken: String): LoginResponse {
        redisUtils.findByRefreshToken(refreshToken)
            ?: throw InvalidCredentialException("만료되거나 찾을 수 없는 Refresh 토큰입니다. 재로그인이 필요합니다.")

        val newTokenInfo = jwtPlugin.validateRefreshTokenAndCreateToken(refreshToken)
        with(redisUtils) {
            deleteByRefreshToken(refreshToken)
            saveRefreshToken(newTokenInfo.refreshToken)
        }
        return newTokenInfo
    }

    override fun updateName(request: NameRequest, userPrincipal: UserPrincipal): NameResponse {
        val user = usersRepository.findByIdOrNull(userPrincipal.id) ?: throw RuntimeException("임시")
        if (user.id != userPrincipal.id) {
            throw RuntimeException("인증되지 않은 사용자")
        }
        if (usersRepository.existsByName(request.name)) {
            throw RuntimeException("이미 사용하고 있는 이름")
        }

        user.name = request.name
        usersRepository.save(user)
        return user.toUpdateNameResponse()
    }

    override fun updatePassword(request: PasswordRequest, userPrincipal: UserPrincipal): PasswordResponse {
        val user = usersRepository.findByIdOrNull(userPrincipal.id) ?: throw RuntimeException("임시")
        if (user.id != userPrincipal.id) {
            throw RuntimeException("인증되지 않은 사용자")
        }
        if (usersRepository.existsByPassword(request.password)) {
            throw RuntimeException("이미 사용하고 있는 패스워드")
        }

        user.password = passwordEncoder.encode(request.password)
        usersRepository.save(user)
        return user.toUpdatePasswordResponse()
    }

    fun isTokenBlacklisted(token: String): Boolean {
        return redisUtils.getData(token) != null
    }
}