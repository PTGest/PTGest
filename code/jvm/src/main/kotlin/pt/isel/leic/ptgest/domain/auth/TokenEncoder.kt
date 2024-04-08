package pt.isel.leic.ptgest.domain.auth

interface TokenEncoder {
    fun hashToken(token: String): String
}
