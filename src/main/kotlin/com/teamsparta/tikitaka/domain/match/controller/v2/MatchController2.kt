package com.teamsparta.tikitaka.domain.match.controller.v2

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.match.dto.MatchResponse
import com.teamsparta.tikitaka.domain.match.dto.MyTeamMatchResponse
import com.teamsparta.tikitaka.domain.match.dto.PostMatchRequest
import com.teamsparta.tikitaka.domain.match.dto.UpdateMatchRequest
import com.teamsparta.tikitaka.domain.match.model.SortCriteria
import com.teamsparta.tikitaka.domain.match.service.v2.MatchService2
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.infra.security.CustomPreAuthorize
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v2/matches")
class MatchController2(
    private val matchService: MatchService2,
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
        val match = matchService.getMatch(matchId)

        return preAuthorize.matchPermission(principal, match) {
            ResponseEntity.status(HttpStatus.OK)
                .body(matchService.updateMatch(principal, matchId, request, match))
        }
    }

    @DeleteMapping("/{match-id}")
    fun deleteMatch(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "match-id") matchId: Long,
    ): ResponseEntity<MatchResponse> {
        val match = matchService.getMatch(matchId)

        return preAuthorize.matchPermission(principal, match) {
            ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(matchService.deleteMatch(principal, matchId, match))
        }
    }

    @GetMapping()
    fun getMatches(
        @RequestParam matchDate: String,
        @RequestParam(required = false) regions: List<Region>?,
        @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<MatchResponse>> {
        println("Received date: $matchDate, region: $regions")
        val matches = matchService.getMatchesByDateAndRegion(pageable, LocalDate.parse(matchDate), regions)
        return ResponseEntity.status(HttpStatus.OK).body(matches)
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
        @PathVariable("region") regions: List<String>,
        @RequestParam("sort", defaultValue = "CREATED_AT") sort: String,
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<MatchResponse>> {
        val trimmedRegions = regions.map { it.trim() }
        val regions = trimmedRegions.map { Region.fromString(it) }
        val sort = SortCriteria.fromString(sort)
        return ResponseEntity.status(HttpStatus.OK)
            .body(matchService.getMatchesByRegionAndSort(regions, pageable, sort))
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

    @GetMapping("/my-team-matches")
    fun getMyTeamMatches(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PageableDefault(size = 10) pageable: Pageable,
        @RequestParam matchStatus: Boolean?
    ): ResponseEntity<Page<MyTeamMatchResponse>> {
        return preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER, TeamRole.SUB_LEADER, TeamRole.MEMBER)) {
            ResponseEntity.status(HttpStatus.OK).body(
                matchService.getMyTeamMatches(principal, pageable, matchStatus)
            )
        }
    }
}
