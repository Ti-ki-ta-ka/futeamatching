package com.teamsparta.tikitaka.domain.evaluation.service.v3

import com.teamsparta.tikitaka.domain.users.dto.EmailDto
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EvaluationEmailServiceImpl(
    private val javaMailSender: JavaMailSender,
    @Value("\${spring.mail.username}") private val sendEmail: String
) : EvaluationEmailService {

    override fun sendMessage(): String {
        val body = """
            <h3>경기가 완료되었습니다</h3>
            <h3>상대팀을 평가해 주세요</h3>
            <h3>평가를 진행하지 않으면 다른 서비스를 이용할 수 없습니다</h3>
            <h3>감사합니다.</h3>
        """.trimIndent()
        return body
    }

    override fun createEMail(email: String?, text: String?): MimeMessage {
        val message: MimeMessage = javaMailSender.createMimeMessage()

        message.setFrom(sendEmail)
        message.setRecipients(MimeMessage.RecipientType.TO, email)
        message.subject = "futeamatching 평가"
        val body = """
            <h1>$text</h1>
        """.trimIndent()
        message.setText(body, "UTF-8", "html")

        return message
    }

    override fun sendEmail(email: String, text: String) {
        val message = createEMail(email, text)
        try {
            javaMailSender.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalArgumentException("메일 발송 중 오류가 발생했습니다.")
        }
    }

    override fun emailCheck(request: EmailDto): String {
        val authCode = sendMessage()
        sendEmail(request.email, authCode)

        return authCode
    }
}



