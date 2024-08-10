package com.teamsparta.tikitaka.infra.security.config

import com.teamsparta.tikitaka.infra.security.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val accessDeniedHandler: AccessDeniedHandler
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api/v1/users/log-in",
                    "/api/v1/users/sign-up",
                    "/error",
                    "/api/v1/matches/**",
                    "/api/v1/users/token/refresh",
                    "/api/v1/matches/searches",
                    "/api/v1/matches/by-deadline",
                    "/api/v1/matches/available",
                    "/api/v2/users/log-in",
                    "/api/v2/users/sign-up",
                    "/api/v2/users/token/refresh",
                    "/api/v2/matches/searches",
                    "/api/v2/matches/by-deadline",
                    "/api/v2/matches/available",
                    "/oauth/**",
                    "/login/oauth2/**",
                    "/api/v2/login/oauth2/code/kakao",
                    "/favicon.ico",
                    "/resources/**",
                    "/static/**",
                    "/public/**",
                    "/webui/**",
                    "/h2-console/**",
                    "/api/v2/oauth/kakako"
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntryPoint)
                it.accessDeniedHandler(accessDeniedHandler)
            }
            .build()
    }
}