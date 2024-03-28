package pt.isel.leic.ptgest.http.controllers.auth

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.controllers.auth.model.request.LoginRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.request.SignupRequest
import pt.isel.leic.ptgest.http.controllers.auth.model.response.HttpResponse
import pt.isel.leic.ptgest.http.controllers.auth.model.response.LoginResponse
import pt.isel.leic.ptgest.http.controllers.auth.model.response.SignupResponse
import pt.isel.leic.ptgest.http.utils.Uris
import pt.isel.leic.ptgest.http.utils.setCookie
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
        val tokenDetails = services.login(
            userInfo.email,
            userInfo.password
        )

        setCookie(
            "access_token",
            tokenDetails.token,
            tokenDetails.expirationDate,
            true,
            response
        )

        return HttpResponse.ok(
            message = "User logged in successfully.",
            details = LoginResponse(
                token = tokenDetails.token,
                expirationDate = tokenDetails.expirationDate
            )
        )
    }
}