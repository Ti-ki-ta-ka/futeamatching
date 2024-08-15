package com.teamsparta.tikitaka.domain.recruitment.model

import com.teamsparta.tikitaka.domain.common.baseentity.BaseEntity
import com.teamsparta.tikitaka.domain.recruitment.dto.UpdateRecruitmentRequest
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "recruitment")
@SQLRestriction("deleted_at is null")
class Recruitment(

    @Column(name = "team_id", nullable = false)
    val teamId: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    var recruitType: RecruitType,

    @Column(name = "quantity", nullable = false)
    var quantity: Int,

    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "closing_status", nullable = false)
    var closingStatus: Boolean = false,

    ) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun updateRecruitment(
        request: UpdateRecruitmentRequest,
    ) {
        this.content = request.content
        this.quantity = request.quantity
        this.recruitType = RecruitType.fromString(request.recruitType)
    }

    companion object {
        fun of(
            teamId: Long,
            userId: Long,
            recruitType: String,
            quantity: Int,
            content: String,
            closingStatus: Boolean,
        ): Recruitment {
            return Recruitment(
                teamId = teamId,
                userId = userId,
                recruitType = RecruitType.fromString(recruitType),
                quantity = quantity,
                content = content,
                closingStatus = closingStatus,
            )
        }
    }


}