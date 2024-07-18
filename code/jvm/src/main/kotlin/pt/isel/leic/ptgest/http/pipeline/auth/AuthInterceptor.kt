package pt.isel.leic.ptgest.http.pipeline.auth

import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import pt.isel.leic.ptgest.domain.auth.model.AuthTokenDetails
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.utils.AuthenticationRequired
import pt.isel.leic.ptgest.http.utils.revokeCookies
import pt.isel.leic.ptgest.http.utils.setCookies
import pt.isel.leic.ptgest.services.AuthService
import pt.isel.leic.ptgest.services.JwtService
import pt.isel.leic.ptgest.services.errors.AuthError
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
        if (handler is HandlerMethod && isAuthRequired(handler)) {
            val tokenDetails = try {
                processTokens(request, response)
            } catch (e: Exception) {
                revokeCookies(response)
                throw e
            }

            verifyRole(handler, tokenDetails)

            if (handler.methodParameters.any { it.parameterType == AuthenticatedUser::class.java }) {
                AuthenticatedUserResolver.addUserTo(
                    AuthenticatedUser(
                        id = tokenDetails.userId,
                        role = tokenDetails.role
                    ),
                    request
                )
            }
        }
        return true
    }

    private fun isAuthRequired(handler: HandlerMethod): Boolean =
        handler.method.isAnnotationPresent(AuthenticationRequired::class.java) ||
            handler.beanType.isAnnotationPresent(AuthenticationRequired::class.java)

    private fun verifyRole(handler: HandlerMethod, tokenDetails: AuthTokenDetails) {
        val authenticationRequiredFromClass = handler.beanType.getAnnotation(AuthenticationRequired::class.java)?.role
        val authenticationRequiredFromMethod = handler.method.getAnnotation(AuthenticationRequired::class.java)?.role

        val authenticationRequired = if (authenticationRequiredFromMethod?.isNotEmpty() == true) {
            authenticationRequiredFromMethod
        } else if (authenticationRequiredFromClass?.isNotEmpty() == true) {
            authenticationRequiredFromClass
        } else {
            return
        }

        if (authenticationRequired.none { it == tokenDetails.role }) {
            throw AuthError.UserAuthenticationError.UnauthorizedRole
        }
    }

    private fun processTokens(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): AuthTokenDetails {
        val currentDate = Date()

        val authHeaders = request.getHeaders("Authorization").toList()

        val accessToken = request.cookies?.firstOrNull { it.name == "access_token" }?.value
            ?: authHeaders.firstOrNull { it.startsWith("Bearer access_token=") }?.removePrefix("Bearer access=")

        val refreshToken = request.cookies?.firstOrNull { it.name == "refresh_token" }?.value
            ?: authHeaders.firstOrNull { it.startsWith("Bearer refresh_token=") }?.removePrefix("Bearer refresh=")

        return when {
            accessToken != null -> {
                try {
                    jwtService.extractToken(accessToken)
                } catch (e: ExpiredJwtException) {
                    if (refreshToken == null) {
                        throw AuthError.UserAuthenticationError.TokenNotProvided
                    } else {
                        refreshTokens(refreshToken, response, currentDate)
                    }
                }
            }
            refreshToken != null -> {
                refreshTokens(refreshToken, response, currentDate)
            }
            else -> throw AuthError.UserAuthenticationError.TokenNotProvided
        }
    }

    private fun refreshTokens(
        refreshToken: String,
        response: HttpServletResponse,
        currentDate: Date
    ): AuthTokenDetails {
        val tokens = authService.refreshToken(refreshToken, currentDate)

        setCookies(response, tokens, currentDate)

        response.addHeader("Authorization", "Bearer access_token=${tokens.accessToken.token}")
        response.addHeader("Authorization", "Bearer refresh_token=${tokens.refreshToken.token}")

        return jwtService.extractToken(refreshToken)
    }
}
