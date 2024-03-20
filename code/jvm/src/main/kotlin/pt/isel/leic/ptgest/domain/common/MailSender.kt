package pt.isel.leic.ptgest.domain.common

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailSender(private val mailSender: JavaMailSender) {

    @Value("\${spring.mail.username}")
    private lateinit var from: String

    fun sendMail(to: String, subject: String, body: String) {
        throw NotImplementedError()
    }
}