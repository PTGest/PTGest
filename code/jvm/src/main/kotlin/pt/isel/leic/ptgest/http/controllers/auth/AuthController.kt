package pt.isel.leic.ptgest.http.controllers.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.domain.auth.model.TokenPair
import pt.isel.leic.ptgest.http.controllers.auth.model.request.LoginRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.RefreshTokenRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.SignupRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.response.HttpResponse
import pt.isel.leic.ptgest.http.controllers.auth.model.response.LoginResponse
import pt.isel.leic.ptgest.http.controllers.auth.model.response.SignupResponse
import pt.isel.leic.ptgest.http.utils.Uris
import pt.isel.leic.ptgest.http.utils.setCookie
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
                val accessToken = sessionCookies.firstOrNull { it.name == "access_token" }?.value
                val refreshToken = sessionCookies.firstOrNull { it.name == "refresh_token" }?.value

                if (accessToken == null || refreshToken == null) {
                    throw AuthError.UserAuthenticationError.TokenNotProvided
                }

                val tokens = services.refreshToken(accessToken, refreshToken)

                setCookies(response, tokens)

                tokens
            }
            refreshTokenBody != null -> {
                val accessTokenParts = request.getHeader("Authorization")?.split(" ")
                    ?: throw AuthError.UserAuthenticationError.TokenNotProvided

                if (accessTokenParts.size != 2) {
                    throw BadCredentialsException("Invalid Authorization header")
                }

                services.refreshToken(accessTokenParts[1], refreshTokenBody.refreshToken)
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

    @DeleteMapping(Uris.Auth.LOGOUT)
    fun logout(response: HttpServletResponse): ResponseEntity<*> {
        throw NotImplementedError("Not implemented")
    }

    private fun setCookies(response: HttpServletResponse, tokens: TokenPair) {
        setCookie(
            "access_token",
            tokens.accessToken.token,
            tokens.accessToken.expirationDate,
            response
        )

        setCookie(
            "refresh_token",
            tokens.refreshToken.token,
            tokens.refreshToken.expirationDate,
            response
        )
    }
}
