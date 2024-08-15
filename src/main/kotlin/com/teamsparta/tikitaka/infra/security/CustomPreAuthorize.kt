package com.teamsparta.tikitaka.infra.security

import com.teamsparta.tikitaka.domain.common.exception.AccessDeniedException
import com.teamsparta.tikitaka.domain.match.model.Match
import com.teamsparta.tikitaka.domain.match.model.matchapplication.MatchApplication
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class CustomPreAuthorize {
    fun <T> hasAnyRole(principal: UserPrincipal, validRoles: Set<TeamRole>, action: () -> T): T {
        val validAuthorities = validRoles.map { SimpleGrantedAuthority("ROLE_$it") }.toSet()
        if (principal.authorities.none { it in validAuthorities }) {
            throw AccessDeniedException("Not allowed to this API")
        }
        return action()
    }

    fun <T> applicationPermission(principal: UserPrincipal, matchApply: MatchApplication, action: () -> T): T {
        if (matchApply.applyUserId != principal.id &&
            !principal.authorities.contains(SimpleGrantedAuthority("ROLE_LEADER"))
        ) {
            throw AccessDeniedException("You do not have permission to perform this action.")
        }
        return action()
    }

    fun <T> matchPermission(principal: UserPrincipal, match: Match, action: () -> T): T {
        if (match.userId != principal.id &&
            !principal.authorities.contains(SimpleGrantedAuthority("ROLE_LEADER"))
        ) {
            throw AccessDeniedException("You do not have permission to perform this action.")
        }
        return action()
    }
}

