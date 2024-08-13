package com.teamsparta.tikitaka.domain.users.service.v3


import com.teamsparta.tikitaka.domain.common.util.RedisUtils
import com.teamsparta.tikitaka.domain.users.dto.EmailDto
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.security.MessageDigest
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class EmailServiceImpl(
    private val javaMailSender: JavaMailSender,
    private val redisUtils: RedisUtils,
    @Value("\${spring.mail.username}") private val sendEmail: String
) : EmailService {
    override fun setContext(code: String): String {
        val context = Context()
        val templateEngine = SpringTemplateEngine()
        val templateResolver = ClassLoaderTemplateResolver()


        context.setVariable("code", code)

        templateResolver.prefix = "templates/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.isCacheable = false

        templateEngine.setTemplateResolver(templateResolver)

        return templateEngine.process("mail", context)
    }

    override fun createEMail(email: String): MimeMessage {
        val key = StringBuilder()

        repeat(8) {
            val index = Random.nextInt(3)

            when (index) {
                0 -> key.append((Random.nextInt(26) + 97).toChar()) // 소문자
                1 -> key.append((Random.nextInt(26) + 65).toChar()) // 대문자
                2 -> key.append(Random.nextInt(10)) // 숫자
            }
        }

        val message: MimeMessage = javaMailSender.createMimeMessage()
        message.setFrom(sendEmail)
        message.setRecipients(MimeMessage.RecipientType.TO, email)
        message.subject = "futeamatching 이메일 인증"
        val body = """
            <h3>요청하신 인증 번호입니다.</h3>
            <h1>$key</h1>
            <h3>감사합니다.</h3>
        """.trimIndent()
        message.setText(body, "UTF-8", "html")
        redisUtils.setDataExpireEmail(email, key.toString(), 10 * 60L)

        return message
    }

    override fun sendEmail(email: String) {
        if (redisUtils.existData(email)) {
            redisUtils.deleteData(email)
        }

        val emailForm = createEMail(email)
        javaMailSender.send(emailForm)
    }
    
    override fun verificationEmail(email: String, number: String): Boolean {
        val codeFoundByEmail = redisUtils.getData(email)
        return codeFoundByEmail?.equals(number) ?: false
    }

    override fun makeMemberId(email: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(email.toByteArray())
        md.update(LocalDateTime.now().toString().toByteArray())
        return md.digest().joinToString("") { String.format("%02x", it) }
    }
}