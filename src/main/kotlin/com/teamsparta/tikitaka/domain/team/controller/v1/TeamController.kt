package com.teamsparta.tikitaka.domain.team.controller.v1


import com.teamsparta.tikitaka.domain.team.Service.v1.TeamService
import com.teamsparta.tikitaka.domain.team.dto.request.TeamRequest
import com.teamsparta.tikitaka.domain.team.dto.response.TeamResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/teams")
@RestController
class TeamController(
    private val teamService: TeamService
) {


    @PostMapping
    fun createTeam(
        @RequestBody request: TeamRequest
    ): ResponseEntity<TeamResponse> {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(request))
    }

    @PutMapping("/{team-id}")
    fun updateTeam(
        @PathVariable("team-id") teamId: Long,
        @RequestBody request: TeamRequest
    ): ResponseEntity<TeamResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.updateTeam(request, teamId))
    }

    @DeleteMapping("/{team-id}")
    fun deleteTeam(
        @PathVariable("team-id") teamId: Long
    ): ResponseEntity<Unit> {
        teamService.deleteTeam(teamId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @GetMapping
    fun getTeams(
    ): ResponseEntity<List<TeamResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeams())
    }

    @GetMapping("/{team-id}")
    fun getTeam(
        @PathVariable("team-id") teamId: Long
    ): ResponseEntity<TeamResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(teamService.getTeam(teamId))
    }


}