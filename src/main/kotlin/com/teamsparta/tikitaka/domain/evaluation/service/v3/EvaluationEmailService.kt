package com.teamsparta.tikitaka.domain.evaluation.service.v3

import com.teamsparta.tikitaka.domain.users.dto.EmailDto
import jakarta.mail.internet.MimeMessage

interface EvaluationEmailService {
    fun sendMessage(): String
    fun createEMail(email: String?, text: String?): MimeMessage
    fun sendEmail(email: String, text: String)
    fun emailCheck(request: EmailDto): String
}