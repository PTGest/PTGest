package pt.isel.leic.ptgest.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.model.user.response.UserDetailsResponse
import pt.isel.leic.ptgest.services.user.UserService
import java.util.*

@RestController
@RequestMapping(Uris.PREFIX)
class UserController(private val service: UserService) {
    @GetMapping(Uris.User.USER_DETAILS)
    fun getUserDetails(
        @PathVariable id: UUID,
    ): ResponseEntity<*> {
        val userDetails = service.getUserDetails(id)

        val responseData = when (userDetails.role) {
            Role.TRAINEE -> {
                val traineeDetails = service.getTraineeDetails(id)
                UserDetailsResponse.TraineeUserDetails(userDetails, traineeDetails)
            }
            Role.HIRED_TRAINER, Role.INDEPENDENT_TRAINER -> {
                val trainerDetails = service.getTrainerDetails(id)
                UserDetailsResponse.TrainerUserDetails(userDetails, trainerDetails)
            }
            Role.COMPANY -> UserDetailsResponse.CompanyUserDetails(userDetails)
        }

        return HttpResponse.ok(
            message = "User details retrieved successfully",
            details = responseData
        )
    }
}
