package pt.isel.leic.ptgest.http.controllers

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import pt.isel.leic.ptgest.domain.auth.AuthenticatedUser
import pt.isel.leic.ptgest.http.model.request.*
import pt.isel.leic.ptgest.http.utils.Uris

@RestController
@RequestMapping(Uris.AUTH_PREFIX)
class AuthController {

    @PostMapping(Uris.Auth.SIGNUP_TRAINEE)
    fun signupTrainee(
        @Valid @RequestBody
        traineeInfo: SignupTraineeRequest
    ) {
    }

    @PostMapping(Uris.Auth.SIGNUP_INDEPENDENT_TRAINER)
    fun signupIndependentTrainer(
        @Valid @RequestBody
        trainerInfo: SignupIndependentTrainerRequest
    ) {
    }

    @PostMapping(Uris.Auth.SIGNUP_HIRED_TRAINER)
    fun signupHiredTrainer(
        @Valid @RequestBody
        trainerInfo: SignupHiredTrainerRequest
    ) {
    }

    @PostMapping(Uris.Auth.SIGNUP_COMPANY)
    fun signupCompany(
        @Valid @RequestBody
        companyInfo: SignupCompanyRequest
    ) {
    }

    @PostMapping(Uris.Auth.LOGIN)
    fun login(
        @RequestBody
        userInfo: LoginRequest
    ) {
    }

    @DeleteMapping(Uris.Auth.LOGOUT)
    fun logout(
        authenticatedUser: AuthenticatedUser
    ) {
    }

    @PostMapping(Uris.Auth.VALIDATE_TOKEN)
    fun validateToken(
        authenticatedUser: AuthenticatedUser
    ) {
    }

}