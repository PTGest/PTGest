package pt.isel.leic.ptgest.http.pipeline.auth

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.leic.ptgest.domain.auth.model.AccessTokenDetails
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.utils.RequiredRole
import pt.isel.leic.ptgest.http.utils.setCookies
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.AuthService
import pt.isel.leic.ptgest.services.auth.JwtService
import java.util.*

@Component
class AuthInterceptor(
    private val authService: AuthService,
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
            val user = processTokens(request, response)

            val requiredRoleFromClass = handler.beanType.getAnnotation(RequiredRole::class.java)?.role
            val requiredRoleFromMethod = handler.method.getAnnotation(RequiredRole::class.java)?.role

            if ((requiredRoleFromClass != null && !requiredRoleFromClass.contains(user.role)) ||
                (requiredRoleFromMethod != null && !requiredRoleFromMethod.contains(user.role))
            ) {
                throw AuthError.UserAuthenticationError.UnauthorizedRole
            }

            val authenticatedUser = AuthenticatedUser(
                id = user.userId,
                role = user.role
            )

            AuthenticatedUserResolver.addUserTo(authenticatedUser, request)
        }
        return true
    }

    private fun processTokens(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): AccessTokenDetails {
        val currentDate = Date()

        val accessTokenCookie = request.cookies?.firstOrNull { it.name == "access_token" }?.value
        val refreshTokenCookie = request.cookies?.firstOrNull { it.name == "refresh_token" }?.value

        val accessTokenHeader = request.getHeader("Authorization")?.removePrefix("Bearer ")
        val refreshTokenHeader = request.getHeader("Refresh-Token")?.removePrefix("Bearer ")

        val accessToken = accessTokenCookie ?: accessTokenHeader
        val refreshToken = refreshTokenCookie ?: refreshTokenHeader

        return when {
            accessToken != null -> {
                try {
                    jwtService.extractToken(accessToken)
                } catch (e: ExpiredJwtException) {
                    if (refreshToken == null) {
                        throw AuthError.UserAuthenticationError.TokenNotProvided
                    } else {
                        val tokens = authService.refreshToken(refreshToken, currentDate)

                        setCookies(response, tokens, currentDate)

                        jwtService.extractToken(refreshToken)
                    }
                }
            }
            refreshToken != null -> {
                val tokens = authService.refreshToken(refreshToken, currentDate)

                setCookies(response, tokens, currentDate)

                jwtService.extractToken(refreshToken)
            }
            else -> throw AuthError.UserAuthenticationError.TokenNotProvided
        }
    }
}
