package pt.isel.leic.ptgest.domain.auth

import java.security.MessageDigest
import java.util.*

class Sha256TokenEncoder: TokenEncoder {
    override fun hashToken(token: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")

        return Base64.getUrlEncoder().encodeToString(
            messageDigest.digest(Charsets.UTF_8.encode(token).array())
        )
    }

}