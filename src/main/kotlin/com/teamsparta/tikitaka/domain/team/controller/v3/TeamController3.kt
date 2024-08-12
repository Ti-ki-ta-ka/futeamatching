package com.teamsparta.tikitaka.domain.team.controller.v3


import com.teamsparta.tikitaka.domain.team.dto.request.TeamRequest
import com.teamsparta.tikitaka.domain.team.dto.response.PageResponse
import com.teamsparta.tikitaka.domain.team.dto.response.TeamResponse
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.domain.team.service.v3.TeamService3
import com.teamsparta.tikitaka.infra.security.CustomPreAuthorize
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v3/teams")
@RestController
class TeamController3(
    private val teamService: TeamService3,
    private val preAuthorize: CustomPreAuthorize
) {
    @GetMapping("/searches")
    fun searchTeams(
        @RequestParam("region", required = false) region: String?,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "10") size: Int,
        @RequestParam("sort_by", defaultValue = "createdAt") sortBy: String,
        @RequestParam("sort_direction", defaultValue = "desc") direction: String,
        @RequestParam(value = "name") name: String,

        ): ResponseEntity<PageResponse<TeamResponse>> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(teamService.searchTeamListByName(region, page, size, sortBy, direction, name))
    }

    @PostMapping
    fun createTeam(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody request: TeamRequest
    ): ResponseEntity<TeamResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(principal, request))
    }

    @PutMapping("/{team-id}")
    fun updateTeam(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "team-id") teamId: Long,
        @RequestBody request: TeamRequest
    ): ResponseEntity<TeamResponse> {
        return preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER)) {
            ResponseEntity.status(HttpStatus.OK).body(teamService.updateTeam(principal.id, request, teamId))
        }
    }

    @DeleteMapping("/{team-id}")
    fun deleteTeam(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "team-id") teamId: Long
    ): ResponseEntity<Unit> {
        preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER)) {
            teamService.deleteTeam(principal.id, teamId)
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()

    }

    @GetMapping
    fun getTeams(
        @RequestParam("region", required = false) region: String?,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "10") size: Int,
        @RequestParam("sort_by", defaultValue = "created_at") sortBy: String,
        @RequestParam("sort_direction", defaultValue = "desc") direction: String
    ): ResponseEntity<PageResponse<TeamResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeams(region, page, size, sortBy, direction))
    }


    @GetMapping("/{team-id}")
    fun getTeam(
        @PathVariable(name = "team-id") teamId: Long
    ): ResponseEntity<TeamResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeam(teamId))
    }


}