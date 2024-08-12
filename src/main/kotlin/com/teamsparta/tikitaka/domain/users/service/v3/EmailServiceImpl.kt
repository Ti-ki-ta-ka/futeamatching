package com.teamsparta.tikitaka.domain.users.service.v3

import com.teamsparta.tikitaka.domain.users.dto.EmailRequest
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class EmailServiceImpl(
    private val javaMailSender: JavaMailSender,
) : EmailService {
    private val sendEmail = "tikitakaad1111@gmail.com"

    override fun createNumber(): String {
        val key = StringBuilder()

        repeat(8) {
            val index = Random.nextInt(3)

            when (index) {
                0 -> key.append((Random.nextInt(26) + 97).toChar()) // 소문자
                1 -> key.append((Random.nextInt(26) + 65).toChar()) // 대문자
                2 -> key.append(Random.nextInt(10)) // 숫자
            }
        }
        return key.toString()
    }

    override fun createEMail(email: String?, number: String?): MimeMessage {
        val message: MimeMessage = javaMailSender.createMimeMessage()

        message.setFrom(sendEmail)
        message.setRecipients(MimeMessage.RecipientType.TO, email)
        message.subject = "futeamatching 이메일 인증"
        val body = """
            <h3>요청하신 인증 번호입니다.</h3>
            <h1>$number</h1>
            <h3>감사합니다.</h3>
        """.trimIndent()
        message.setText(body, "UTF-8", "html")

        return message
    }

    override fun sendVerificationEmail(email: String, number: String) {
        val message = createEMail(email, number)
        try {
            javaMailSender.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalArgumentException("메일 발송 중 오류가 발생했습니다.")
        }
    }

    override fun emailCheck(request: EmailRequest): String {
        val authCode = createNumber()
        sendVerificationEmail(request.email, authCode)

        return authCode
    }
}