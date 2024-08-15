package com.teamsparta.tikitaka.domain.team.model.teammember

import com.teamsparta.tikitaka.domain.team.model.Team
import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "team_member")
@SQLRestriction("deleted_at is null")
class TeamMember(
    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    val team: Team,

    @Enumerated(EnumType.STRING)
    var teamRole: TeamRole = TeamRole.MEMBER,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun softDelete() {
        this.deletedAt = LocalDateTime.now()
    }
}