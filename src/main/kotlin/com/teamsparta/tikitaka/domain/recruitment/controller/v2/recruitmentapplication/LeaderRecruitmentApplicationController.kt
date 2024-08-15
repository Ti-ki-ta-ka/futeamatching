package com.teamsparta.tikitaka.domain.recruitment.controller.v2.recruitmentapplication

import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.RecruitmentApplicationResponse
import com.teamsparta.tikitaka.domain.recruitment.dto.recruitmentapplication.UpdateApplicationResponseStatus
import com.teamsparta.tikitaka.domain.recruitment.service.v2.recruitmentapplication.LeaderRecruitmentApplicationService
import com.teamsparta.tikitaka.domain.team.model.teammember.TeamRole
import com.teamsparta.tikitaka.infra.security.CustomPreAuthorize
import com.teamsparta.tikitaka.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v2/leader/recruitments/{recruitment-id}/recruitment-applications")
class LeaderRecruitmentApplicationController(
    private val preAuthorize: CustomPreAuthorize,
    private val applicationService: LeaderRecruitmentApplicationService,
) {
    @PatchMapping("/{recruitment-application-id}")
    fun replyRecruitmentApplication(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable(name = "recruitment-id") recruitmentId: Long,
        @PathVariable(name = "recruitment-application-id") applicationId: Long,
        @RequestBody responseStatus: UpdateApplicationResponseStatus
    ): ResponseEntity<RecruitmentApplicationResponse> {
        return preAuthorize.hasAnyRole(principal, setOf(TeamRole.LEADER)) {
            ResponseEntity.status(HttpStatus.OK)
                .body(
                    applicationService.replyRecruitmentApplication(
                        principal.id,
                        recruitmentId,
                        applicationId,
                        responseStatus
                    )
                )
        }
    }
}
