package com.teamsparta.tikitaka.domain.match.controller.v1

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.match.dto.MatchResponse
import com.teamsparta.tikitaka.domain.match.dto.PostMatchRequest
import com.teamsparta.tikitaka.domain.match.dto.UpdateMatchRequest
import com.teamsparta.tikitaka.domain.match.model.SortCriteria
import com.teamsparta.tikitaka.domain.match.service.v1.MatchService
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.infra.security.CustomPreAuthorize
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/matches")
class MatchController(
    private val matchService: MatchService,
    private val preAuthorize: CustomPreAuthorize,
) {
    @PostMapping("/create")
    fun postMatch(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody request: PostMatchRequest,
    ): ResponseEntity<MatchResponse> {
        return preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER, TeamRole.SUB_LEADER)) {
            ResponseEntity.status(HttpStatus.CREATED)
                .body(matchService.postMatch(principal, request))
        }
    }

    @PutMapping("/{match-id}")
    fun updateMatch(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "match-id") matchId: Long,
        @RequestBody request: UpdateMatchRequest,
    ): ResponseEntity<MatchResponse> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(matchService.updateMatch(principal, matchId, request))
    }

    @DeleteMapping("/{match-id}")
    fun deleteMatch(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "match-id") matchId: Long,
    ): ResponseEntity<MatchResponse> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(matchService.deleteMatch(principal, matchId))
    }

    @GetMapping()
    fun getMatches(
        @PageableDefault(size = 20, sort = ["createdAt,desc"]) pageable: Pageable
    ): ResponseEntity<Page<MatchResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(matchService.getMatches(pageable))
    }

    @GetMapping("/available")
    fun getAvailableMatchesAndSort(
        @RequestParam("sort", defaultValue = "CREATED_AT") sort: String,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<MatchResponse>> {
        val sort = SortCriteria.fromString(sort)
        return ResponseEntity.status(HttpStatus.OK).body(matchService.getAvailableMatchesAndSort(pageable, sort))
    }

    @GetMapping("/region/{region}")
    fun getMatchesByRegionAndSort(
        @PathVariable("region") region: String,
        @RequestParam("sort", defaultValue = "CREATED_AT") sort: String,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<MatchResponse>> {
        val trimmedRegion = region.trim()
        val region = Region.fromString(trimmedRegion)
        val sort = SortCriteria.fromString(sort)
        return ResponseEntity.status(HttpStatus.OK)
            .body(matchService.getMatchesByRegionAndSort(region, pageable, sort))
    }

    @GetMapping("/{match-id}")
    fun getMatchDetails(
        @PathVariable("match-id") matchId: Long,
    ): ResponseEntity<MatchResponse> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(matchService.getMatchDetails(matchId))
    }

    @GetMapping("/searches")
    fun searchMatch(
        pageable: Pageable,
        @RequestParam keyword: String,
        @RequestParam("sort", defaultValue = "CREATED_AT") sort: String,
    ): ResponseEntity<Page<MatchResponse>> {
        val sort = SortCriteria.fromString(sort)
        return ResponseEntity.status(HttpStatus.OK).body(matchService.searchMatch(pageable, keyword, sort))
    }


}
