package com.teamsparta.tikitaka.domain.team.controller.v2

import com.teamsparta.tikitaka.domain.team.dto.response.TeamMemberResponse
import com.teamsparta.tikitaka.domain.team.service.v2.TeamMemberService
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v2/team-members")
@RestController
class TeamMemberController(
    private val teamMemberService: TeamMemberService,
) {

    @GetMapping()
    fun getMyTeamMembers(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<List<TeamMemberResponse>> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(teamMemberService.getMyTeamMembers(principal.id))
    }

}