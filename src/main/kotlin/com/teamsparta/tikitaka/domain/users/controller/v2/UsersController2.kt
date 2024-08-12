package com.teamsparta.tikitaka.domain.users.controller.v2

import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.domain.recruitment.service.v2.recruitmentapplication.RecruitmentApplicationService
import com.teamsparta.tikitaka.domain.users.dto.LoginRequest
import com.teamsparta.tikitaka.domain.users.dto.LoginResponse
import com.teamsparta.tikitaka.domain.users.dto.NameRequest
import com.teamsparta.tikitaka.domain.users.dto.NameResponse
import com.teamsparta.tikitaka.domain.users.dto.PasswordRequest
import com.teamsparta.tikitaka.domain.users.dto.PasswordResponse
import com.teamsparta.tikitaka.domain.users.dto.SignUpRequest
import com.teamsparta.tikitaka.domain.users.dto.TokenRefreshDto
import com.teamsparta.tikitaka.domain.users.dto.UserDto
import com.teamsparta.tikitaka.domain.users.service.v2.UsersService2
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v2/users")
@RestController
class UsersController2(
    private val userService: UsersService2,
    private val recruitmentApplicationService: RecruitmentApplicationService,
) {
    @PostMapping("/sign-up")
    fun signUp(
        @RequestBody signUpRequest: SignUpRequest
    ): ResponseEntity<UserDto> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.signUp(signUpRequest))
    }

    @PostMapping("/log-in")
    fun logIn(
        @RequestBody request: LoginRequest
    ): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(userService.logIn(request))
    }

    @PostMapping("/log-out")
    fun logout(request: HttpServletRequest): ResponseEntity<Unit> {
        val token = request.getAttribute("accessToken") as String?
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.logOut(token!!))
    }

    @PostMapping("/token/refresh")
    fun tokenRefresh(@RequestBody tokenRefreshDto: TokenRefreshDto): ResponseEntity<LoginResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.validateRefreshTokenAndCreateToken(tokenRefreshDto.refreshToken))
    }

    @PutMapping("/profile/name")
    fun updateName(
        @RequestBody request: NameRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<NameResponse> {
        return ResponseEntity.ok(userService.updateName(request, userPrincipal))
    }

    @PutMapping("/profile/password")
    fun updatePassword(
        @RequestBody request: PasswordRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<PasswordResponse> {
        return ResponseEntity.ok(userService.updatePassword(request, userPrincipal))
    }

    @GetMapping("my-recruitment-applications")
    fun getMyApplications(
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<List<RecruitmentApplicationResponse>> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(recruitmentApplicationService.getMyApplications(userPrincipal))
    }
}