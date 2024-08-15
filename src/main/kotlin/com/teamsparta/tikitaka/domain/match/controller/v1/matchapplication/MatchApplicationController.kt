package com.teamsparta.tikitaka.domain.match.controller.v1.matchapplication

import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MatchApplicationResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.MyApplicationsResponse
import com.teamsparta.tikitaka.domain.match.dto.matchapplication.ReplyApplicationRequest
import com.teamsparta.tikitaka.domain.match.service.v1.matchapplication.MatchApplicationService
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.infra.security.CustomPreAuthorize
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/matches")
@RestController
class MatchApplicationController(
    private val matchApplicationService: MatchApplicationService,
    private val preAuthorize: CustomPreAuthorize,
) {
    @PostMapping("/{match-id}/match-applications")
    fun applyMatch(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "match-id") matchId: Long,
    ): ResponseEntity<MatchApplicationResponse> {
        return preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER, TeamRole.SUB_LEADER)) {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(matchApplicationService.applyMatch(principal.id, matchId))
        }
    }

    @DeleteMapping("/{match-id}/match-applications/{application-id}")
    fun deleteMatchApplication(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "application-id") applicationId: Long,
    ): ResponseEntity<Unit> {
        matchApplicationService.deleteMatchApplication(principal, applicationId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @PatchMapping("/{match-id}/match-applications/{application-id}")
    fun replyMatchApplication(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "application-id") applicationId: Long,
        @RequestBody request: ReplyApplicationRequest
    ): ResponseEntity<MatchApplicationResponse> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(matchApplicationService.replyMatchApplication(principal.id, applicationId, request))
    }

    @GetMapping("/match-applications/my-applications")
    fun getMyApplications(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ResponseEntity<List<MyApplicationsResponse>> {
        return ResponseEntity.ok(matchApplicationService.getMyApplications(principal.id))
    }
}