package pt.isel.leic.ptgest.http.controllers

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
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.model.auth.request.AuthenticatedSignupRequest
import pt.isel.leic.ptgest.http.model.auth.request.ChangePasswordRequest
import pt.isel.leic.ptgest.http.model.auth.request.ForgetPasswordRequest
import pt.isel.leic.ptgest.http.model.auth.request.LoginRequest
import pt.isel.leic.ptgest.http.model.auth.request.ResetPasswordRequest
import pt.isel.leic.ptgest.http.model.auth.request.SignupRequest
import pt.isel.leic.ptgest.http.model.auth.response.AuthenticatedSignupResponse
import pt.isel.leic.ptgest.http.model.auth.response.LoginResponse
import pt.isel.leic.ptgest.http.model.auth.response.RefreshTokenResponse
import pt.isel.leic.ptgest.http.utils.AuthenticationRequired
import pt.isel.leic.ptgest.http.utils.createTokensHeaders
import pt.isel.leic.ptgest.http.utils.revokeCookies
import pt.isel.leic.ptgest.http.utils.setCookies
import pt.isel.leic.ptgest.services.AuthService
import pt.isel.leic.ptgest.services.errors.AuthError
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
    @AuthenticationRequired(Role.COMPANY, Role.INDEPENDENT_TRAINER)
    fun authenticatedSignup(
        @Valid @RequestBody
        userInfo: AuthenticatedSignupRequest,
        authenticatedUser: AuthenticatedUser
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
                    details = AuthenticatedSignupResponse(traineeId)
                )
            }
            is AuthenticatedSignupRequest.HiredTrainer -> {
                if (authenticatedUser.role != Role.COMPANY) {
                    throw AuthError.UserAuthenticationError.UnauthorizedRole
                }

                val trainerId = service.signUpHiredTrainer(
                    authenticatedUser.id,
                    userInfo.name,
                    userInfo.email,
                    userInfo.gender,
                    userInfo.capacity,
                    userInfo.phoneNumber
                )
                return HttpResponse.created(
                    message = "Hired Trainer registered successfully.",
                    details = AuthenticatedSignupResponse(trainerId)
                )
            }
        }
    }

    @PostMapping(Uris.Auth.FORGET_PASSWORD)
    fun forgetPassword(
        @Valid @RequestBody
        forgetInfo: ForgetPasswordRequest
    ): ResponseEntity<*> {
        service.forgetPassword(forgetInfo.email)

        return HttpResponse.ok(
            message = "Password reset email sent successfully."
        )
    }

    @GetMapping(Uris.Auth.VALIDATE_PASSWORD_RESET_REQUEST)
    fun validatePasswordResetRequest(
        @PathVariable requestToken: String
    ): ResponseEntity<*> {
        service.validatePasswordResetRequest(requestToken.trim())

        return HttpResponse.ok(
            message = "Password reset token validated successfully."
        )
    }

    @PutMapping(Uris.Auth.RESET_PASSWORD)
    fun resetPassword(
        @PathVariable requestToken: String,
        @Valid @RequestBody
        resetInfo: ResetPasswordRequest
    ): ResponseEntity<*> {
        service.resetPassword(requestToken.trim(), resetInfo.password)

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
            details = LoginResponse(authenticationDetails),
            headers = createTokensHeaders(authenticationDetails.tokens)
        )
    }

    @PostMapping(Uris.Auth.REFRESH)
    fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val refreshTokenCookie = request.cookies.firstOrNull { it.name == "refresh_token" }?.value
        val refreshTokenHeader = request.getHeader("Refresh-Token")?.removePrefix("Bearer ")

        val currentDate = Date()

        require(!refreshTokenCookie.isNullOrBlank() || !refreshTokenHeader.isNullOrBlank()) { "Refresh token not provided" }

        val refreshToken = refreshTokenCookie ?: refreshTokenHeader ?: throw BadCredentialsException("Invalid refresh token")

        val tokens = service.refreshToken(
            refreshToken,
            currentDate
        )

        setCookies(response, tokens, currentDate)

        return HttpResponse.ok(
            message = "Token refreshed successfully.",
            details = RefreshTokenResponse(tokens),
            headers = createTokensHeaders(tokens)
        )
    }

    @AuthenticationRequired
    @GetMapping(Uris.Auth.VALIDATE_AUTHENTICATION)
    fun validateAuthentication(): ResponseEntity<*> {
        return HttpResponse.ok(
            message = "User authenticated successfully."
        )
    }

    @AuthenticationRequired
    @PutMapping(Uris.Auth.CHANGE_PASSWORD)
    fun changePassword(
        @Valid @RequestBody
        passwordRequest: ChangePasswordRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        service.changePassword(authenticatedUser.id, passwordRequest.currentPassword, passwordRequest.newPassword)

        return HttpResponse.ok(
            message = "Password changed successfully."
        )
    }

    @PostMapping(Uris.Auth.LOGOUT)
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        if (request.cookies != null) {
            revokeCookies(response)
        }

        return HttpResponse.ok(
            message = "User logged out successfully."
        )
    }
}
