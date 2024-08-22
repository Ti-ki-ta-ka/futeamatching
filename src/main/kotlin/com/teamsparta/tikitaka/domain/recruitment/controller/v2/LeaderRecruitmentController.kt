package com.teamsparta.tikitaka.domain.recruitment.controller.v2

import com.teamsparta.tikitaka.domain.recruitment.dto.PostRecruitmentRequest
import com.teamsparta.tikitaka.domain.recruitment.dto.RecruitmentResponse
import com.teamsparta.tikitaka.domain.recruitment.dto.UpdateRecruitmentRequest
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.domain.recruitment.service.v2.LeaderRecruitmentService
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
@RequestMapping("/api/v2/leader/recruitments")
class LeaderRecruitmentController(
    private val preAuthorize: CustomPreAuthorize,
    private val leaderRecruitmentService: LeaderRecruitmentService,
) {

    @PostMapping()
    fun postRecruitment(
        @AuthenticationPrincipal principal: UserPrincipal, @RequestBody request: PostRecruitmentRequest
    ): ResponseEntity<RecruitmentResponse> {
        return preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER)) {
            ResponseEntity.status(HttpStatus.CREATED).body(leaderRecruitmentService.postRecruitment(principal, request))
        }
    }

    @PutMapping("/{recruitment-id}")
    fun updateRecruitmentPost(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "recruitment-id") recruitmentId: Long,
        @RequestBody request: UpdateRecruitmentRequest
    ): ResponseEntity<RecruitmentResponse> {
        return preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER)) {
            ResponseEntity.status(HttpStatus.OK)
                .body(leaderRecruitmentService.updateRecruitmentPost(principal.id, recruitmentId, request))
        }
    }

    @PatchMapping("/{recruitment-id}")
    fun closeRecruitmentPost(
        @AuthenticationPrincipal principal: UserPrincipal, @PathVariable(name = "recruitment-id") recruitmentId: Long
    ): ResponseEntity<RecruitmentResponse> {
        return preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER)) {
            ResponseEntity.status(HttpStatus.OK)
                .body(leaderRecruitmentService.closeRecruitmentPost(principal.id, recruitmentId))
        }
    }

    @DeleteMapping("/{recruitment-id}")
    fun deleteRecruitmentPost(
        @AuthenticationPrincipal principal: UserPrincipal, @PathVariable(name = "recruitment-id") recruitmentId: Long
    ): ResponseEntity<Unit> = preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER)) {
        leaderRecruitmentService.deleteRecruitmentPost(principal.id, recruitmentId)
        ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @GetMapping("/{recruitment-id}/recruitment-applications")
    fun getRecruitmentApplications(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "recruitment-id") recruitmentId: Long,
        @PageableDefault(size = 10) pageable: Pageable,
        @RequestParam responseStatus: String?
    ): ResponseEntity<Page<RecruitmentApplicationResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            leaderRecruitmentService.getRecruitmentApplications(
                principal.id,
                recruitmentId,
                pageable,
                responseStatus
            )
        )
    }

    @GetMapping()
    fun getMyTeamRecruitmentPosts(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<Page<RecruitmentResponse>> {
        return preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER)) {
            ResponseEntity.status(HttpStatus.OK)
                .body(leaderRecruitmentService.getMyTeamRecruitments(principal, pageable))
        }
    }
}


