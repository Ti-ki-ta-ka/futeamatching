package com.teamsparta.tikitaka.domain.users.service.v3

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.common.exception.InvalidCredentialException
import com.teamsparta.tikitaka.domain.common.exception.ModelNotFoundException
import com.teamsparta.tikitaka.domain.common.util.RedisUtils
import com.teamsparta.tikitaka.domain.team.repository.teamMember.TeamMemberRepository
import com.teamsparta.tikitaka.domain.users.dto.CodeResponse
import com.teamsparta.tikitaka.domain.users.dto.LoginRequest
import com.teamsparta.tikitaka.domain.users.dto.LoginResponse
import com.teamsparta.tikitaka.domain.users.dto.NameRequest
import com.teamsparta.tikitaka.domain.users.dto.NameResponse
import com.teamsparta.tikitaka.domain.users.dto.PasswordRequest
import com.teamsparta.tikitaka.domain.users.dto.PasswordResponse
import com.teamsparta.tikitaka.domain.users.dto.SignUpRequest
import com.teamsparta.tikitaka.domain.users.dto.UserDto
import com.teamsparta.tikitaka.domain.users.model.Users
import com.teamsparta.tikitaka.domain.users.repository.UsersRepository
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import com.teamsparta.tikitaka.infra.security.jwt.JwtPlugin
import jakarta.mail.MessagingException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UsersServiceImpl3(
    private val usersRepository: UsersRepository,
    private val passwordEncoder: PasswordEncoder,
    private val teamMemberRepository: TeamMemberRepository,
    private val jwtPlugin: JwtPlugin,
    private val redisUtils: RedisUtils,
    private val emailService: EmailService
) : UsersService3 {

    private val verificationCodes = mutableMapOf<String, String>()

    override fun createCode(
        email: String
    ): CodeResponse {
        val authCode = emailService.createNumber()
        verificationCodes[email] = authCode

        try {
            emailService.sendVerificationEmail(email, authCode)
        } catch (e: MessagingException) {
            throw IllegalArgumentException("메일 발송 중 오류가 발생했습니다.")
        }
        return CodeResponse(authCode)
    }

    @Transactional
    override fun signUp(
        request: SignUpRequest,
        code: String
    ): UserDto {
        if (usersRepository.findByEmail(request.email) != null) {
            throw InvalidCredentialException("중복된 이메일입니다.")
        }
        if (request.password != request.confirmPassword) {
            throw InvalidCredentialException("비밀번호 확인이 비밀번호와 일치하지 않습니다.")
        }
        with(Users) {
            validateEmail(request.email)
            validatePassword(request.password)
            validateName(request.name)
        }

        val user = usersRepository.save(
            Users(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                name = request.name
            )
        )

        val validCode =
            verificationCodes[request.email] ?: throw AccessDeniedException("인증된 이메일이 아닙니다")
        if (validCode != code) {
            throw InvalidCredentialException("유효하지 않거나 만료된 인증 코드입니다.")
        }
        verificationCodes.remove(request.email)
        val enabled = usersRepository.findByEmail(request.email)
        enabled?.emailEnabled = true
        usersRepository.save(enabled!!)

        return UserDto.fromEntity(user)
    }

    override fun logIn(request: LoginRequest): LoginResponse {
        val user = usersRepository.findByEmail(request.email) ?: throw ModelNotFoundException("Users", null)
        if (!user.emailEnabled) throw AccessDeniedException("이메일 인증이 되지 않았습니다")
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw InvalidCredentialException("비밀번호가 일치하지 않습니다")
        }

        val role = when {
            user.teamStatus -> {
                val teamMember = teamMemberRepository.findByUserId(user.id!!)
                teamMember.teamRole.name
            }

            else -> null
        }
        val accessToken = jwtPlugin.generateAccessToken(
            subject = user.id.toString(),
            email = user.email,
        )
        val refreshToken = jwtPlugin.generateRefreshToken(
            subject = user.id.toString(),
            email = user.email,
        )
        redisUtils.saveRefreshToken(refreshToken)
        return LoginResponse(
            userId = user.id!!,
            userName = user.name,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
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
        val user =
            usersRepository.findByIdOrNull(userPrincipal.id) ?: throw ModelNotFoundException("Users", userPrincipal.id)
        if (user.id != userPrincipal.id) {
            throw InvalidCredentialException("인증되지 않은 사용자입니다")
        }
        if (usersRepository.existsByName(request.name)) {
            throw IllegalArgumentException("이미 사용하고 있는 이름입니다")
        }
        user.updateName(request.name)
        usersRepository.save(user)
        return NameResponse.from(user)
    }

    override fun updatePassword(request: PasswordRequest, userPrincipal: UserPrincipal): PasswordResponse {
        val user =
            usersRepository.findByIdOrNull(userPrincipal.id) ?: throw ModelNotFoundException("Users", userPrincipal.id)
        if (user.id != userPrincipal.id) {
            throw InvalidCredentialException("인증되지 않은 사용자입니다")
        }
        if (passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("기존에 사용한 패스워드입니다")
        }
        Users.validatePassword(request.password)
        user.updatePassword(passwordEncoder.encode(request.password))
        usersRepository.save(user)
        return PasswordResponse.from(user)
    }

    fun isTokenBlacklisted(token: String): Boolean {
        return redisUtils.getData(token) != null
    }
}
