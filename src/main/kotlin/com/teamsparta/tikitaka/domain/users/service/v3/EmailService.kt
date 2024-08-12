package com.teamsparta.tikitaka.domain.users.service.v3

import com.teamsparta.tikitaka.domain.users.dto.EmailRequest
import jakarta.mail.internet.MimeMessage

interface EmailService {
    fun createNumber(): String
    fun createEMail(email: String?, number: String?): MimeMessage
    fun sendVerificationEmail(email: String, number: String)
    fun emailCheck(request: EmailRequest): String
}