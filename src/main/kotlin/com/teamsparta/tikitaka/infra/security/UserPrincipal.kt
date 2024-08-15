package com.teamsparta.tikitaka.infra.security

import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserPrincipal(
    val id: Long,
    val name: String,
    val authorities: Collection<GrantedAuthority>
) {
    constructor(
        id: Long,
        name: String,
        role: TeamRole?,
    ) : this(id, name, role?.let { setOf(SimpleGrantedAuthority("ROLE_$it")) } ?: emptySet<GrantedAuthority>())
}