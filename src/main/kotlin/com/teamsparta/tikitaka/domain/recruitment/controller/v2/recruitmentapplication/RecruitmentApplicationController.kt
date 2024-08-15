package com.teamsparta.tikitaka.domain.recruitment.controller.v2.recruitmentapplication

import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.domain.recruitment.service.v2.recruitmentapplication.RecruitmentApplicationService
import com.teamsparta.tikitaka.infra.security.CustomPreAuthorize
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2/recruitments/{recruitment-id}/recruitment-applications")
class RecruitmentApplicationController(
    private val preAuthorize: CustomPreAuthorize,
    private val recruitmentApplicationService: RecruitmentApplicationService,
) {
    @PostMapping
    fun applyRecruitment(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "recruitment-id") recruitmentId: Long,
    ): ResponseEntity<RecruitmentApplicationResponse> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(recruitmentApplicationService.applyRecruitment(principal.id, recruitmentId))

    }

    @PatchMapping("/{application-id}")
    fun cancelApplication(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "recruitment-id") recruitmentId: Long,
        @PathVariable(name = "application-id") applicationId: Long,
    ): ResponseEntity<RecruitmentApplicationResponse> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(recruitmentApplicationService.cancelApplication(principal, recruitmentId, applicationId))
    }


}

