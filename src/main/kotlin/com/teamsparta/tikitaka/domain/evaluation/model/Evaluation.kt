package com.teamsparta.tikitaka.domain.evaluation.model

import com.teamsparta.tikitaka.domain.evaluation.dto.EvaluationRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "evaluation")
@SQLRestriction("deleted_at is null")
class Evaluation(
    @Column(name = "evaluator_team_id", nullable = false)
    val evaluatorTeamId: Long,

    @Column(name = "evaluatee_team_id", nullable = false)
    val evaluateeTeamId: Long,

    @Column(name = "evaluator_id", nullable = false)
    val evaluatorId: Long,

    @Column(name = "manner_score", nullable = false)
    var mannerScore: Int = 0,

    @Column(name = "skill_score", nullable = false)
    var skillScore: Int = 0,

    @Column(name = "attendance_score", nullable = false)
    var attendanceScore: Int = 0,

    @Column(name = "evaluation_status", nullable = false)
    var evaluationStatus: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: LocalDateTime? = null

) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun updateEvaluation(
        request: EvaluationRequest
    ) {
        validateMannerScore(request.mannerScore)
        validateSkillScore(request.skillScore)
        validateAttendanceScore(request.attendanceScore)
        this.mannerScore = request.mannerScore
        this.skillScore = request.skillScore
        this.attendanceScore = request.attendanceScore
    }

    private fun validateMannerScore(score: Int) {
        if (score < 0 || score > 100) {
            throw IllegalArgumentException("올바른 값을 입력해주세요!")
        }
    }

    private fun validateSkillScore(score: Int) {
        if (score < 0 || score > 100) {
            throw IllegalArgumentException("올바른 값을 입력해주세요!")
        }
    }

    private fun validateAttendanceScore(score: Int) {
        if (score < 0 || score > 100) {
            throw IllegalArgumentException("올바른 값을 입력해주세요!")
        }
    }
}