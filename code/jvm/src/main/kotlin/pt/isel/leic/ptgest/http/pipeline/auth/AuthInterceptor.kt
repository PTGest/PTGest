package pt.isel.leic.ptgest.http.pipeline.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.utils.setCookie
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.AuthService
import java.time.LocalDate

@Component
class AuthInterceptor(private val authService: AuthService) : HandlerInterceptor {

    //  TODO: Check if the update of the expiration date is correct
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (handler is HandlerMethod && handler.methodParameters
                .any { it.parameterType == AuthenticatedUser::class.java }
        ) {
            val sessionCookie = request.cookies?.firstOrNull { it.name == "access_token" }?.value

            val token = sessionCookie ?: request.getHeader("Authorization")
            ?: throw AuthError.UserAuthenticationError.TokenNotProvided

            val updatedExpirationDate = authService.validateToken(token)

            val user =
                if (token.startsWith("Bearer")) {
                    processAuthorizationHeaderValue(token)
                } else {
                    processCookieValue(token, updatedExpirationDate, response)
                }

            AuthenticatedUserResolver.addUserTo(user, request)
        }
        return true
    }

    private fun processAuthorizationHeaderValue(authorizationValue: String): AuthenticatedUser {
        val headerParts = authorizationValue.trim().split(" ")
        if (headerParts.size != 2) {
            throw BadCredentialsException("Invalid Authorization header")
        }

        return authService.getUserIdByToken(headerParts[1])
    }

    private fun processCookieValue(
        cookieValue: String,
        updatedExpirationDate: LocalDate,
        response: HttpServletResponse
    ): AuthenticatedUser {
        setCookie(
            "access_token",
            cookieValue,
            updatedExpirationDate,
            true,
            response
        )

        return authService.getUserIdByToken(cookieValue)
    }

}