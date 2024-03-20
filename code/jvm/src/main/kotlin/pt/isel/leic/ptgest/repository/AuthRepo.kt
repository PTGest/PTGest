package pt.isel.leic.ptgest.repository

interface AuthRepo {

    fun getToken(tokenHash: String): TokenDe
}