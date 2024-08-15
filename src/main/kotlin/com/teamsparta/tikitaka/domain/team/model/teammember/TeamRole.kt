package com.teamsparta.tikitaka.domain.team.model.teammember

enum class TeamRole {
    LEADER, SUB_LEADER, MEMBER;

    companion object {
        fun fromString(teamRole: String): TeamRole {
            return try {
                valueOf(teamRole.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid category : $teamRole")
            }
        }
    }
}