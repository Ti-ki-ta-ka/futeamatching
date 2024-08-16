package com.teamsparta.tikitaka.infra.security.jwt

import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.team.repository.teammember.TeamMemberRepository
import com.teamsparta.tikitaka.domain.users.service.v1.UsersServiceImpl
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtPlugin: JwtPlugin,
    private val usersService: UsersServiceImpl,
    private val teamMemberRepository: TeamMemberRepository

) : OncePerRequestFilter() {

    companion object {
        private val BEARER_PATTERN = Regex("^Bearer (.+?)$")
    }

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val jwt = request.getBearerToken()

        if (jwt != null) {
            if (usersService.isTokenBlacklisted(jwt)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted")
                return
            }
            jwtPlugin.validateToken(jwt).onSuccess { claims ->
                val userId = claims.payload.subject.toLong()
                val name = claims.payload.get("email", String::class.java)
                val role: TeamRole? = try {
                    val teamMember = teamMemberRepository.findByUserId(userId)
                    teamMember.teamRole
                } catch (e: Exception) {
                    null
                }


                val principal = UserPrincipal(
                    id = userId, name = name, role = role
                )

                val authentication = JwtAuthenticationToken(
                    principal = principal, details = WebAuthenticationDetailsSource().buildDetails(request)
                )
                SecurityContextHolder.getContext().authentication = authentication
                request.setAttribute("accessToken", jwt)
            }.onFailure {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
                return
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun HttpServletRequest.getBearerToken(): String? {
        val header = this.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        val prefix = "Bearer "
        return if (header.startsWith(prefix, ignoreCase = true)) header.substring(prefix.length)
        else null
    }
}