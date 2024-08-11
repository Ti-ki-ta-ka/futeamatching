package com.teamsparta.tikitaka.domain.team.controller.v3.teammember

import com.teamsparta.tikitaka.domain.team.dto.response.RemoveMemberResopnse
import com.teamsparta.tikitaka.domain.team.dto.response.TeamMemberResponse
import com.teamsparta.tikitaka.domain.team.service.v3.teammember.TeamMemberService3
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v3/team-members")
@RestController
class TeamMemberController3(
    private val teamMemberService: TeamMemberService3,
) {

    @GetMapping()
    fun getMyTeamMembers(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<List<TeamMemberResponse>> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(teamMemberService.getMyTeamMembers(principal.id))
    }

    @DeleteMapping("/{team-member-id}/remove")
    fun leaveTeam(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable("team-member-id") teamMemberId: Long,
    ): ResponseEntity<RemoveMemberResopnse> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(teamMemberService.leaveTeam(principal.id, teamMemberId))
    }
}