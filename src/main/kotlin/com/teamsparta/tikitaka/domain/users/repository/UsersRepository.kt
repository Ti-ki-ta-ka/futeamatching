package com.teamsparta.tikitaka.domain.users.repository

import com.teamsparta.tikitaka.domain.users.model.Users
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository : JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?

    fun existsByName(name: String): Boolean

    fun findByOAuthProviderId(oauthProviderId: String): Users?
}