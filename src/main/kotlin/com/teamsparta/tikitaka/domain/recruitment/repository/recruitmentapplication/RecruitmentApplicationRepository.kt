package com.teamsparta.tikitaka.domain.recruitment.repository.recruitmentapplication

import com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication.RecruitmentApplication
import com.teamsparta.tikitaka.domain.recruitment.model.recruitmentapplication.ResponseStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecruitmentApplicationRepository : JpaRepository<RecruitmentApplication, Long>,
    CustomRecruitmentApplicationRepository {
    fun findByRecruitmentId(recruitmentId: Long): List<RecruitmentApplication>?
    fun findByIdAndRecruitmentId(ApplicationId: Long, RecruitmentId: Long): RecruitmentApplication?
    fun findByUserId(UserId: Long): List<RecruitmentApplication>?
    fun findByRecruitmentIdAndResponseStatus(
        recruitmentId: Long,
        responseStatus: ResponseStatus
    ): List<RecruitmentApplication>
}