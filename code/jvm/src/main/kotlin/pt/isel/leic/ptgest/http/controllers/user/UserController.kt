package pt.isel.leic.ptgest.http.controllers.user

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.http.controllers.user.model.ProfileResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.services.user.UserService

@RestController
@RequestMapping(Uris.PREFIX)
class UserController(private val service: UserService) {

    @GetMapping(Uris.User.PROFILE)
    fun getProfile(
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        when (authenticatedUser.role) {
            Role.TRAINEE -> {
                val traineeDetails = service.getTraineeDetails(authenticatedUser.id)

                return HttpResponse.ok(
                    message = "Trainee profile retrieved successfully",
                    details = ProfileResponse.TraineeProfile(
                        name = traineeDetails.name,
                        email = traineeDetails.email,
                        gender = traineeDetails.gender,
                        birthdate = traineeDetails.birthdate,
                        phoneNumber = traineeDetails.phoneNumber
                    )
                )
            }
            Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER -> {
                val traineeDetails = service.getTrainerDetails(authenticatedUser.id)

                return HttpResponse.ok(
                    message = "Trainer profile retrieved successfully",
                    details = ProfileResponse.TrainerProfile(
                        name = traineeDetails.name,
                        email = traineeDetails.email,
                        gender = traineeDetails.gender,
                        phoneNumber = traineeDetails.phoneNumber
                    )
                )
            }
            Role.COMPANY -> {
                val companyDetails = service.getCompanyDetails(authenticatedUser.id)

                return HttpResponse.ok(
                    message = "Company profile retrieved successfully",
                    details = ProfileResponse.CompanyProfile(
                        name = companyDetails.name,
                        email = companyDetails.email
                    )
                )
            }
        }
    }
}
