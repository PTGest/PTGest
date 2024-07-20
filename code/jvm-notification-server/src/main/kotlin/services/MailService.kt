package services

import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MailService(
    private val username: String,
    private val password: String
) {
    fun sendMail(
        destination: String,
        subject: String,
        text: String
    ) {
        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(username, password)
        })

        val message = MimeMessage(session)
        message.setFrom(InternetAddress(username))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination))
        message.subject = subject
        message.setText(text)

        Transport.send(message)
    }

    companion object {
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }
    }

}