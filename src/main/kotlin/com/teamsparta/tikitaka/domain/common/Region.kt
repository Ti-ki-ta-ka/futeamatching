package com.teamsparta.tikitaka.domain.common

enum class Region {
    SEOUL,
    BUSAN,
    DAEGU,
    INCHEON,
    GWANGJU,
    DAEJEON,
    ULSAN,
    SEJONG,
    GYEONGGI,
    GANGWON,
    CHUNGCHEONG,
    JEOLLA,
    GYEONGSANG,
    JEJU;

    companion object {
        fun fromString(value: String): Region {
            return try {
                valueOf(value.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid region: $value")
            }
        }
    }
}