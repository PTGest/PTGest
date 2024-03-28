package pt.isel.leic.ptgest.http.pipeline.auth

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.leic.ptgest.domain.auth.AuthDomain
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.utils.setCookie
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.JwtService
import java.util.*

@Component
class AuthInterceptor(
    private val jwtService: JwtService,
    private val authDomain: AuthDomain
) : HandlerInterceptor {

    //  TODO: Check if the update of the expiration date is correct
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (handler is HandlerMethod && handler.methodParameters
                .any { it.parameterType == AuthenticatedUser::class.java }
        ) {
            val sessionCookie = request.cookies?.firstOrNull { it.name == "access_token" }

            val requestHeader = request.getHeader("Authorization")

            val user =
                when {
                    sessionCookie != null && sessionCookie.name.isNotEmpty() -> {
                        processCookieValue(sessionCookie, response)
                    }

                    requestHeader != null && requestHeader.startsWith("Bearer") -> {
                        processAuthorizationHeaderValue(requestHeader)
                    }

                    else -> throw AuthError.UserAuthenticationError.TokenNotProvided
                }

            AuthenticatedUserResolver.addUserTo(user, request)
        }
        return true
    }

    //  TODO: Check how is the correct way to update the expiration date in the header
    private fun processAuthorizationHeaderValue(authorizationValue: String): AuthenticatedUser {
        val headerParts = authorizationValue.trim().split(" ")
        if (headerParts.size != 2) {
            throw BadCredentialsException("Invalid Authorization header")
        }

        val tokenDetails = jwtService.extractToken(headerParts[1])

        return AuthenticatedUser(
            id = tokenDetails.userId,
            role = tokenDetails.role
        )
    }

    private fun processCookieValue(
        cookie: Cookie,
        response: HttpServletResponse
    ): AuthenticatedUser {
        val tokenDetails = jwtService.extractToken(cookie.value)

        if ((tokenDetails.expirationDate.time / 1000).toInt() != cookie.maxAge) {
            throw AuthError.TokenError.TokenExpirationMismatchException
        }

        val currentDate = Date()

        val expirationDate = authDomain.updateTokenExpirationDate(
            tokenDetails.creationDate,
            currentDate
        )

        val cookieValue = jwtService.generateToken(
            tokenDetails.userId,
            tokenDetails.role,
            expirationDate,
            tokenDetails.creationDate
        )

        setCookie(
            "access_token",
            cookieValue,
            expirationDate,
            true,
            response
        )

        return AuthenticatedUser(
            id = tokenDetails.userId,
            role = tokenDetails.role
        )
    }
}