package com.teamsparta.tikitaka.domain.users.service.v3


import jakarta.mail.internet.MimeMessage

interface EmailService {
    fun createEMail(email: String): MimeMessage
    fun sendEmail(email: String)
    fun verificationEmail(email: String, number: String): Boolean
    fun makeMemberId(email: String): String
    fun setContext(code: String): String
}