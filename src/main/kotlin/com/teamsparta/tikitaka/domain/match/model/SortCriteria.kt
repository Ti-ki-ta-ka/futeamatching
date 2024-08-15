package com.teamsparta.tikitaka.domain.match.model

enum class SortCriteria {
    CREATED_AT,
    DEADLINE;

    companion object {
        fun fromString(value: String): SortCriteria {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid sort criteria: $value")
            }
        }
    }
}