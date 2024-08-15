package com.teamsparta.tikitaka.domain.recruitment.model

enum class RecruitType {
    TEAM_MEMBER,
    HIRED_PLAYER;

    companion object {
        fun fromString(value: String): RecruitType {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid RecruitType: $value")
            }
        }
    }
}