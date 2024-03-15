package pt.isel.leic.ptgest.http.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.leic.ptgest.domain.auth.AuthenticatedUser
import pt.isel.leic.ptgest.http.model.request.LoginRequest
import pt.isel.leic.ptgest.http.model.request.SignupRequest
import pt.isel.leic.ptgest.http.model.request.*
import pt.isel.leic.ptgest.http.utils.Uris
import pt.isel.leic.ptgest.services.AuthServices
import java.net.http.HttpResponse

@RestController
@RequestMapping(Uris.AUTH_PREFIX)
class AuthController (private val services: AuthServices) {

    @PostMapping(Uris.Auth.SIGNUP)
    fun signup(
        @Valid @RequestBody
        userInfo: SignupRequest
    ): ResponseEntity<String> {
        return when (userInfo) {
            is SignupRequest.Company ->{
                services.signUp(userInfo.name, userInfo.password)
                ResponseEntity.ok(
                    "Company: ${userInfo.name}, ${userInfo.email}, ${userInfo.password}")
            }

            is SignupRequest.IndependentTrainer -> {
                services.signUp(userInfo.name, userInfo.password)
                ResponseEntity.ok(
                    "Independent Trainer: ${userInfo.name}, ${userInfo.email}, ${userInfo.password}, ${userInfo.gender}, ${userInfo.phoneNumber}"
                )
            }
            is SignupRequest.HiredTrainer -> {
                //ver depois
                ResponseEntity.ok(
                    "Hired Trainer: ${userInfo.name}, ${userInfo.email}, ${userInfo.gender}, ${userInfo.phoneNumber}"
                )
            }
            is SignupRequest.Trainee -> {
                //ver depois
                ResponseEntity.ok(
                    "Trainee: ${userInfo.name}, ${userInfo.email}, ${userInfo.birthdate}, ${userInfo.gender}, ${userInfo.phoneNumber}"
                )
            }
        }
    }

    @PostMapping(Uris.Auth.LOGIN)
    fun login(
        @RequestBody
        userInfo: LoginRequest
    ) : ResponseEntity<*> {
        val info = services.login(userInfo.email,userInfo.password)

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