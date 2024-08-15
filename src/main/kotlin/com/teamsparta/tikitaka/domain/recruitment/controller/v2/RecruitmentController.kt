package com.teamsparta.tikitaka.domain.recruitment.controller.v2

import com.teamsparta.tikitaka.domain.recruitment.dto.RecruitmentResponse
import com.teamsparta.tikitaka.domain.recruitment.service.v2.RecruitmentService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v2/recruitments")
class RecruitmentController(
    private val recruitmentService: RecruitmentService,
) {
    @GetMapping()
    fun getRecruitments(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<RecruitmentResponse>> {
        return ResponseEntity.status(HttpStatus.OK).body(recruitmentService.getRecruitments(pageable))
    }

    @GetMapping("/{recruitment-id}")
    fun getRecruitmentDetails(
        @PathVariable(name = "recruitment-id") recruitmentId: Long,
    ): ResponseEntity<RecruitmentResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(recruitmentService.getRecruitmentDetails(recruitmentId))
    }
}
