package pt.isel.leic.ptgest.http.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.ptgest.domain.auth.AuthenticatedUser
import pt.isel.leic.ptgest.http.model.request.*
import pt.isel.leic.ptgest.http.utils.Uris

@RestController
@RequestMapping(Uris.AUTH_PREFIX)
class AuthController {

    @PostMapping(Uris.Auth.SIGNUP)
    fun signup(
        @Valid @RequestBody
        userInfo: SignupRequest
    ): ResponseEntity<String> {
        return when (userInfo) {
            is SignupRequest.Company ->
                ResponseEntity.ok(
                    "Company: ${userInfo.name}, ${userInfo.email}, ${userInfo.password}")
            is SignupRequest.IndependentTrainer ->
                ResponseEntity.ok(
                    "Independent Trainer: ${userInfo.name}, ${userInfo.email}, ${userInfo.password}, ${userInfo.gender}, ${userInfo.phoneNumber}"
                )
            is SignupRequest.HiredTrainer ->
                ResponseEntity.ok(
                    "Hired Trainer: ${userInfo.name}, ${userInfo.email}, ${userInfo.gender}, ${userInfo.phoneNumber}"
                )
            is SignupRequest.Trainee ->
                ResponseEntity.ok(
                    "Trainee: ${userInfo.name}, ${userInfo.email}, ${userInfo.birthdate}, ${userInfo.gender}, ${userInfo.phoneNumber}"
                )
        }
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

    @GetMapping(Uris.Auth.VALIDATE_TOKEN)
    fun validateToken(
        authenticatedUser: AuthenticatedUser
    ) {
    }

}