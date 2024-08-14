package com.teamsparta.tikitaka.domain.common.util

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class RedisUtils(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    companion object {
        private val DURATION_TIME = 1000 * 60 * 60 * 24L
        private val REFRESH_TOKEN_DURATION_TIME = 1000 * 60 * 60 * 24L * 7
        private const val KEY_PREFIX = "refreshToken"
        private const val VERIFIED_EMAILS_KEY = "verified_emails"
    }

    fun getData(key: String): String? {
        val valueOperations = redisTemplate.opsForValue()
        return valueOperations[key]
    }

    fun setDataExpire(key: String, value: String) {
        val valueOperations = redisTemplate.opsForValue()
        valueOperations.set(key, value, Duration.ofMillis(DURATION_TIME))
    }

    fun deleteData(key: String) {
        redisTemplate.delete(key)
    }

    fun saveRefreshToken(refreshToken: String) {
        val key = "$KEY_PREFIX:$refreshToken"
        redisTemplate.opsForValue().set(key, "", REFRESH_TOKEN_DURATION_TIME, TimeUnit.MILLISECONDS)
    }

    fun findByRefreshToken(refreshToken: String): String? {
        val key = redisTemplate.keys("$KEY_PREFIX:$refreshToken").firstOrNull()
        return key?.let { redisTemplate.opsForValue().get(it) }
    }

    fun deleteByRefreshToken(refreshToken: String) {
        val keys = redisTemplate.keys("$KEY_PREFIX:$refreshToken")
        redisTemplate.delete(keys)
    }

    fun deleteByUserId(userId: String) {
        val keys = redisTemplate.keys("$KEY_PREFIX:$userId:*")
        redisTemplate.delete(keys)
    }

    fun existData(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    fun setDataExpireEmail(key: String, value: String, duration: Long) {
        val valueOperations: ValueOperations<String, String> = redisTemplate.opsForValue()
        val expireDuration = Duration.ofSeconds(duration)
        valueOperations.set(key, value, expireDuration)
    }

    private fun getVerifiedEmailsKey(email: String): String {
        return "$VERIFIED_EMAILS_KEY:$email"
    }

    fun setVerifiedEmail(email: String) {
        val verifiedEmailsKey = getVerifiedEmailsKey(email)
        redisTemplate.opsForSet().add(verifiedEmailsKey, email)
    }

    fun isVerifiedEmail(email: String): Boolean {
        val verifiedEmailsKey = getVerifiedEmailsKey(email)
        return redisTemplate.opsForSet().isMember(verifiedEmailsKey, email) ?: false
    }

    fun deleteEmailData(email: String): Boolean {
        val verifiedEmailsKey = getVerifiedEmailsKey(email)
        return (redisTemplate.opsForSet().remove(verifiedEmailsKey, email) ?: 0) > 0
    }
}