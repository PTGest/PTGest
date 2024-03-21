package pt.isel.leic.ptgest.http.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.model.request.LoginRequest
import pt.isel.leic.ptgest.http.model.request.SignupRequest
import pt.isel.leic.ptgest.http.model.response.HttpResponse
import pt.isel.leic.ptgest.http.model.response.SignupResponse
import pt.isel.leic.ptgest.http.utils.Uris
import pt.isel.leic.ptgest.services.AuthService

@RestController
@RequestMapping(Uris.AUTH_PREFIX)
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

    @PostMapping(Uris.Auth.LOGIN)
    fun login(
        @RequestBody
        userInfo: LoginRequest
    ): ResponseEntity<*> {
        val info = services.login(userInfo.email, userInfo.password)

        return ResponseEntity
            .ok(
                "User logged with success"
            )
    }

    @DeleteMapping(Uris.Auth.LOGOUT)
    fun logout(
        authenticatedUser: AuthenticatedUser
    ) {
    }

    @GetMapping(Uris.Auth.VALIDATE_TOKEN)
    fun validateToken(
        authenticatedUser: AuthenticatedUser
    ) {
    }

}