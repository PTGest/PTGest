package pt.isel.leic.ptgest.http.controllers.auth

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.controllers.auth.model.request.AuthenticatedSignupRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.ForgetPasswordRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.LoginRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.RefreshTokenRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.ResetPasswordRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.SignupRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.response.AuthenticatedSignupResponse
import pt.isel.leic.ptgest.http.controllers.auth.model.response.LoginResponse
import pt.isel.leic.ptgest.http.controllers.auth.model.response.RefreshTokenResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.utils.revokeCookies
import pt.isel.leic.ptgest.http.utils.setCookies
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.auth.AuthService
import java.util.Date

@RestController
@RequestMapping(Uris.PREFIX)
class AuthController(private val service: AuthService) {

    @PostMapping(Uris.Auth.SIGNUP)
    fun signup(
        @Valid @RequestBody
        userInfo: SignupRequest
    ): ResponseEntity<*> {
        return when (userInfo) {
            is SignupRequest.Company -> {
                service.signUpCompany(userInfo.name, userInfo.email, userInfo.password)

                HttpResponse.created(
                    message = "Company registered successfully."
                )
            }

            is SignupRequest.IndependentTrainer -> {
                service.signUpIndependentTrainer(
                    userInfo.name,
                    userInfo.email,
                    userInfo.password,
                    userInfo.gender,
                    userInfo.phoneNumber
                )

                HttpResponse.created(
                    message = "Independent trainer registered successfully."
                )
            }
        }
    }

    @PostMapping(Uris.Auth.AUTHENTICATED_SIGNUP)
    fun authenticatedSignup(
        authenticatedUser: AuthenticatedUser,
        @Valid @RequestBody
        userInfo: AuthenticatedSignupRequest
    ): ResponseEntity<*> {
        when (userInfo) {
            is AuthenticatedSignupRequest.Trainee -> {
                val traineeId = service.signUpTrainee(
                    authenticatedUser.id,
                    authenticatedUser.role,
                    userInfo.name,
                    userInfo.email,
                    userInfo.birthdate,
                    userInfo.gender,
                    userInfo.phoneNumber
                )
                return HttpResponse.created(
                    message = "Trainee registered successfully.",
                    details = AuthenticatedSignupResponse(
                        userId = traineeId
                    )
                )
            }
            is AuthenticatedSignupRequest.HiredTrainer -> {
                val trainerId = service.signUpHiredTrainer(
                    authenticatedUser.id,
                    authenticatedUser.role,
                    userInfo.name,
                    userInfo.email,
                    userInfo.gender,
                    userInfo.capacity,
                    userInfo.phoneNumber
                )
                return HttpResponse.created(
                    message = "Hired Trainer registered successfully.",
                    details = AuthenticatedSignupResponse(
                        userId = trainerId
                    )
                )
            }
        }
    }

    @PostMapping(Uris.Auth.FORGET_PASSWORD)
    fun forgetPassword(
        @Valid @RequestBody
        forgetInfo: ForgetPasswordRequest
    ): ResponseEntity<*> {
        service.reSetPassword(forgetInfo.email)
        return HttpResponse.ok(
            message = "Password reset email sent successfully."
        )
    }

    @GetMapping(Uris.Auth.VALIDATE_PASSWORD_RESET_TOKEN)
    fun validatePasswordResetToken(
        @PathVariable token: String
    ): ResponseEntity<*> {
        service.validatePasswordResetToken(token)
        return HttpResponse.ok(
            message = "Password reset token validated successfully."
        )
    }

    @PutMapping(Uris.Auth.RESET_PASSWORD)
    fun resetPassword(
        @PathVariable token: String,
        @Valid @RequestBody
        resetInfo: ResetPasswordRequest
    ): ResponseEntity<*> {
        service.resetPassword(token, resetInfo.password)
        return HttpResponse.ok(
            message = "Password reset successfully."
        )
    }

    @PostMapping(Uris.Auth.LOGIN)
    fun login(
        @Valid @RequestBody
        userInfo: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val currentDate = Date()

        val authenticationDetails = service.login(
            currentDate,
            userInfo.email,
            userInfo.password
        )

        setCookies(response, authenticationDetails.tokens, currentDate)

        return HttpResponse.ok(
            message = "User logged in successfully.",
            details = LoginResponse(
                role = authenticationDetails.role,
                tokens = authenticationDetails.tokens
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

        val currentDate = Date()

        val tokens = when {
            sessionCookies != null -> {
                val (accessToken, refreshToken) = processCookies(sessionCookies)
                val tokens = service.refreshToken(accessToken, refreshToken, currentDate)

                setCookies(response, tokens, currentDate)
                tokens
            }

            refreshTokenBody != null -> {
                val accessToken = processHeader(request)
                service.refreshToken(accessToken, refreshTokenBody.refreshToken, currentDate)
            }

            else -> {
                throw AuthError.UserAuthenticationError.TokenNotProvided
            }
        }

        return HttpResponse.ok(
            message = "Token refreshed successfully.",
            details = RefreshTokenResponse(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken
            )
        )
    }

    @PostMapping(Uris.Auth.VALIDATE_REFRESH_TOKEN)
    fun validateRefreshToken(
        authenticatedUser: AuthenticatedUser,
        @RequestBody(required = false)
        refreshTokenBody: RefreshTokenRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val sessionCookies = request.cookies

        when {
            sessionCookies != null -> {
                val refreshToken = processCookies(sessionCookies).second

                service.validateRefreshToken(authenticatedUser.id, refreshToken)
            }

            refreshTokenBody != null -> {
                service.validateRefreshToken(authenticatedUser.id, refreshTokenBody.refreshToken)
            }

            else -> {
                throw AuthError.UserAuthenticationError.TokenNotProvided
            }
        }

        return HttpResponse.ok(
            message = "Refresh token validated successfully."
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
                val refreshToken = processCookies(sessionCookies).second

                service.logout(refreshToken)

                revokeCookies(response)
            }

            refreshTokenBody != null -> {
                service.logout(refreshTokenBody.refreshToken)
            }

            else -> {
                throw AuthError.UserAuthenticationError.TokenNotProvided
            }
        }

        return HttpResponse.ok(
            message = "User logged out successfully."
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
