package pt.isel.leic.ptgest.services

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailService(
    private val mailSender: JavaMailSender
) {
    fun sendMail(
        destination: String,
        subject: String,
        text: String
    ) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true)

        helper.setTo(destination)
        helper.setSubject(subject)
        helper.setText(text)

        mailSender.send(message)
    }
}
