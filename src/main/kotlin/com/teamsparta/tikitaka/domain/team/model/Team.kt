package com.teamsparta.tikitaka.domain.team.model

import com.teamsparta.tikitaka.domain.common.Region
import com.teamsparta.tikitaka.domain.common.baseentity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "team")
@SQLRestriction("deleted_at is null")
class Team(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "description")
    var description: String,

    @Column(name = "count_member", nullable = false)
    var countMember: Int = 1,

    @Column(name = "tier")
    var tierScore: Int = 0,

    @Column(name = "manner")
    var mannerScore: Int = 0,

    @Column(name = "attendance")
    var attendanceScore: Int = 0,

    @Column(name = "rank")
    var rank: Long? = null,

    @Column(name = "recruit_status", nullable = false)
    var recruitStatus: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column(name = "region", nullable = false)
    var region: Region

) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


    fun updateTeam(
        name: String,
        description: String,
        region: Region
    ) {
        this.name = name
        this.description = description
        this.region = region
    }
}

