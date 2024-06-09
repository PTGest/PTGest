package pt.isel.leic.ptgest.http.pipeline.auth

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.utils.RequiredRole
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.JwtService

// todo: improve
@Component
class AuthInterceptor(
    private val jwtService: JwtService
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (
            handler is HandlerMethod && handler.methodParameters
                .any { it.parameterType == AuthenticatedUser::class.java }
        ) {
            val sessionCookie = request.cookies?.firstOrNull { it.name == "access_token" }
            val requestHeader = request.getHeader("Authorization")

            val user =
                when {
                    sessionCookie != null && sessionCookie.name.isNotEmpty() -> {
                        processCookieValue(sessionCookie)
                    }

                    requestHeader != null && requestHeader.startsWith("Bearer") -> {
                        processAuthorizationHeaderValue(requestHeader)
                    }

                    else -> throw AuthError.UserAuthenticationError.TokenNotProvided
                }

            val requiredRoleFromClass = handler.beanType.getAnnotation(RequiredRole::class.java)?.role
            val requiredRoleFromMethod = handler.method.getAnnotation(RequiredRole::class.java)?.role

            if ((requiredRoleFromClass != null && !requiredRoleFromClass.contains(user.role)) ||
                (requiredRoleFromMethod != null && !requiredRoleFromMethod.contains(user.role))
            ) {
                throw AuthError.UserAuthenticationError.UnauthorizedRole
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

        val tokenDetails = jwtService.extractToken(headerParts[1])
        return AuthenticatedUser(
            id = tokenDetails.userId,
            role = tokenDetails.role
        )
    }

    private fun processCookieValue(
        cookie: Cookie
    ): AuthenticatedUser {
        val tokenDetails = jwtService.extractToken(cookie.value)

        return AuthenticatedUser(
            id = tokenDetails.userId,
            role = tokenDetails.role
        )
    }
}
