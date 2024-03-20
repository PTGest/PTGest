package pt.isel.leic.ptgest.http.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.ptgest.domain.auth.AuthenticatedUser
import pt.isel.leic.ptgest.http.model.request.LoginRequest
import pt.isel.leic.ptgest.http.model.request.SignupRequest
import pt.isel.leic.ptgest.http.utils.Uris
import pt.isel.leic.ptgest.services.AuthService

@RestController
@RequestMapping(Uris.AUTH_PREFIX)
class AuthController(private val services: AuthService) {

    @PostMapping(Uris.Auth.SIGNUP)
    fun signup(
        @Valid @RequestBody
        userInfo: SignupRequest
    ): ResponseEntity<String> {
        return when (userInfo) {
            is SignupRequest.Company -> {
                services.signUpCompany(userInfo.name, userInfo.email, userInfo.password)
                ResponseEntity.ok("Company")
            }

            is SignupRequest.IndependentTrainer -> {
                services.signUpIndependentTrainer(
                    userInfo.name,
                    userInfo.email,
                    userInfo.password,
                    userInfo.gender,
                    userInfo.phoneNumber
                )
                ResponseEntity.ok("Independent Trainer")
            }

            is SignupRequest.HiredTrainer -> {
                services.signUpHiredTrainer(
                    userInfo.name,
                    userInfo.email,
                    userInfo.gender,
                    userInfo.phoneNumber
                )
                ResponseEntity.ok("Hired Trainer")
            }

            is SignupRequest.Trainee -> {
                services.signUpTrainee(
                    userInfo.name,
                    userInfo.email,
                    userInfo.birthdate,
                    userInfo.gender,
                    userInfo.phoneNumber
                )
                ResponseEntity.ok("Trainee")
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