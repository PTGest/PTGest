package pt.isel.leic.ptgest.http.controllers.auth

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.controllers.auth.model.request.ForgetRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.LoginRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.RefreshTokenRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.ResetPasswordRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.SignupRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.ValidatePasswordResetTokenRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.response.LoginResponse
import pt.isel.leic.ptgest.http.controllers.auth.model.response.SignupResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.utils.Uris
import pt.isel.leic.ptgest.http.utils.revokeCookies
import pt.isel.leic.ptgest.http.utils.setCookies
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.AuthService

@RestController
@RequestMapping(Uris.PREFIX)
class AuthController(private val services: AuthService) {

    @PostMapping(Uris.Auth.SIGNUP)
    fun signup(
        @Valid @RequestBody
        userInfo: SignupRequest
    ): ResponseEntity<*> {
        return when (userInfo) {
            is SignupRequest.Company -> {
                HttpResponse.created(
                    message = "Company registered successfully.",
                    details = SignupResponse(
                        username = userInfo.name,
                        id = services.signUpCompany(userInfo.name, userInfo.email, userInfo.password)
                    )
                )
            }

            is SignupRequest.IndependentTrainer -> {
                HttpResponse.created(
                    message = "Independent trainer registered successfully.",
                    details = SignupResponse(
                        username = userInfo.name,
                        id = services.signUpIndependentTrainer(
                            userInfo.name,
                            userInfo.email,
                            userInfo.password,
                            userInfo.gender,
                            userInfo.phoneNumber
                        )
                    )
                )
            }
        }
    }

    @PostMapping(Uris.Auth.AUTHENTICATED_SIGNUP)
    fun authenticatedSignup(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody
        userInfo: SignupRequest
    ): ResponseEntity<*> {
        throw NotImplementedError("Not implemented")
    }

    @PostMapping(Uris.Auth.FORGET_PASSWORD)
    fun forgetPassword(
        @Valid @RequestBody
        forgetInfo: ForgetRequest
    ): ResponseEntity<*> {
        services.forgetPassword(forgetInfo.email)

        return HttpResponse.ok(
            message = "Password reset email sent successfully.",
            details = null
        )
    }

    @PostMapping(Uris.Auth.VALIDATE_PASSWORD_RESET_TOKEN)
    fun validatePasswordResetToken(
        @Valid @RequestBody
        tokenRequest: ValidatePasswordResetTokenRequest
    ): ResponseEntity<*> {
        services.validatePasswordResetToken(tokenRequest.token)

        return HttpResponse.ok(
            message = "Password reset token validated successfully.",
            details = null
        )
    }

    @PutMapping(Uris.Auth.RESET_PASSWORD)
    fun resetPassword(
        @Valid @RequestBody
        resetInfo: ResetPasswordRequest
    ): ResponseEntity<*> {
        services.resetPassword(resetInfo.token, resetInfo.password)

        return HttpResponse.ok(
            message = "Password reset successfully.",
            details = null
        )
    }

    @PostMapping(Uris.Auth.LOGIN)
    fun login(
        @Valid @RequestBody
        userInfo: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val tokens = services.login(
            userInfo.email,
            userInfo.password
        )

        setCookies(response, tokens)

        return HttpResponse.ok(
            message = "User logged in successfully.",
            details = LoginResponse(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken
            )
        )
    }

    @PostMapping(Uris.Auth.REFRESH)
    fun refreshToken(
        @RequestBody(required = false) refreshTokenBody: RefreshTokenRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val sessionCookies = request.cookies

        val tokens = when {
            sessionCookies != null -> {
                val (accessToken, refreshToken) = processCookies(sessionCookies)

                val tokens = services.refreshToken(accessToken, refreshToken)

                setCookies(response, tokens)

                tokens
            }

            refreshTokenBody != null -> {
                val accessToken = processHeader(request)

                services.refreshToken(accessToken, refreshTokenBody.refreshToken)
            }

            else -> {
                throw AuthError.UserAuthenticationError.TokenNotProvided
            }
        }

        return HttpResponse.ok(
            message = "Token refreshed successfully.",
            details = LoginResponse(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken
            )
        )
    }

    @PostMapping(Uris.Auth.LOGOUT)
    fun logout(
        @RequestBody(required = false) refreshTokenBody: RefreshTokenRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val sessionCookies = request.cookies

        when {
            sessionCookies != null -> {
                val (accessToken, refreshToken) = processCookies(sessionCookies)

                services.logout(accessToken, refreshToken)

                revokeCookies(response)
            }

            refreshTokenBody != null -> {
                val accessToken = processHeader(request)

                services.logout(accessToken, refreshTokenBody.refreshToken)
            }

            else -> {
                throw AuthError.UserAuthenticationError.TokenNotProvided
            }
        }

        return HttpResponse.ok(
            message = "User logged out successfully.",
            details = null
        )
    }

    private fun processCookies(sessionCookies: Array<Cookie>): Pair<String, String> {
        val accessToken = sessionCookies.firstOrNull { it.name == "access_token" }?.value
        val refreshToken = sessionCookies.firstOrNull { it.name == "refresh_token" }?.value

        if (accessToken == null || refreshToken == null) {
            throw AuthError.UserAuthenticationError.TokenNotProvided
        }

        return Pair(accessToken, refreshToken)
    }

    private fun processHeader(request: HttpServletRequest): String {
        val accessTokenParts = request.getHeader("Authorization")?.split(" ")
            ?: throw AuthError.UserAuthenticationError.TokenNotProvided

        if (accessTokenParts.size != 2) {
            throw BadCredentialsException("Invalid Authorization header")
        }

        return accessTokenParts[1]
    }
}
