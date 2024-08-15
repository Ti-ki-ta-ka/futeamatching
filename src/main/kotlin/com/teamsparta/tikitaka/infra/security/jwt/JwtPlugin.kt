package com.teamsparta.tikitaka.infra.security.jwt

import com.teamsparta.tikitaka.domain.common.exception.InvalidCredentialException
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.users.dto.LoginResponse
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class JwtPlugin(
    @Value("\${auth.jwt.issuer}") private val issuer: String,
    @Value("\${auth.jwt.secret}") private val secret: String,
    @Value("\${auth.jwt.accessTokenExpirationHour}") private val accessTokenExpirationHour: Long,
    @Value("\${auth.jwt.refreshTokenExpirationHour}") private val refreshTokenExpirationHour: Long
) {

    fun validateToken(jwt: String): Result<Jws<Claims>> {
        return kotlin.runCatching {
            val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

            Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt)
        }
    }

    fun generateAccessToken(subject: String, email: String): String {
        return generateToken(subject, email, Duration.ofHours(accessTokenExpirationHour))
    }

    fun generateRefreshToken(subject: String, email: String): String {
        return generateToken(subject, email, Duration.ofHours(refreshTokenExpirationHour))
    }

    private fun generateToken(subject: String, email: String, expirationPeriod: Duration): String {
        val claims: Claims = Jwts.claims()
            .add(mapOf("email" to email))
            .build()

        val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
        val now = Instant.now()

        return Jwts.builder()
            .subject(subject)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expirationPeriod)))
            .claims(claims)
            .signWith(key)
            .compact()
    }

    fun validateRefreshTokenAndCreateToken(refreshToken: String): LoginResponse {
        val claims =
            validateToken(refreshToken).getOrElse { throw InvalidCredentialException("Invalid Refresh Token") }.payload
        val userId = claims.subject.toLong()
        val userName = claims.subject
        val subject = claims.subject
        val email = claims["email"].toString()

        val newAccessToken = generateAccessToken(subject, email)
        val newRefreshToken = generateRefreshToken(subject, email)

        return LoginResponse(userId, userName,newAccessToken, newRefreshToken)
    }
}