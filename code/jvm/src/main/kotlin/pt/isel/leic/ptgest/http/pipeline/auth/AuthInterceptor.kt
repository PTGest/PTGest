package pt.isel.leic.ptgest.http.pipeline.auth

import org.springframework.stereotype.Component
import pt.isel.leic.ptgest.domain.auth.AuthenticatedUser
import pt.isel.leic.ptgest.services.AuthService

@Component
class AuthInterceptor(val authService: AuthService) {
    fun processAuthorizationHeaderValue(authorizationValue: String?): AuthenticatedUser? {
//        return if (authorizationValue == null) {
//            null
//        } else {
//            val parts = authorizationValue.trim().split(" ")
//            if (parts.size != 2 || parts[0].lowercase() != BEARER_SCHEME) {
//                return null
//            }
//            val token = parts[1]
//            return authService.getUserIdByToken(token)?.let {
//                val expirationTime = authService.updateLastUsedAt(token)
//                AuthenticatedUser(it, token, expirationTime)
//            }
//        }
        throw NotImplementedError()
    }

    fun processCookieValue(cookieValue: String?): AuthenticatedUser? {
//        return if (cookieValue == null) {
//            null
//        } else {
//            authService.getUserIdByToken(cookieValue)?.let {
//                val expirationTime = authService.updateLastUsedAt(cookieValue)
//                AuthenticatedUser(it, cookieValue, expirationTime)
//            }
//        }
        throw NotImplementedError()
    }

    companion object {
        const val BEARER_SCHEME = "Bearer"
    }
}