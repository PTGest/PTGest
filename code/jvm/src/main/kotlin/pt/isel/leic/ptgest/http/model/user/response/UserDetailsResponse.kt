package pt.isel.leic.ptgest.http.model.user.response

import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.user.model.TraineeDetails
import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import java.util.Date

sealed class UserDetailsResponse {
    data class TraineeUserDetails(
        val name: String,
        val email: String,
        val gender: Gender,
        val birthdate: Date,
        val phoneNumber: String?
    ) : UserDetailsResponse() {
        constructor(userDetails: UserDetails, trainee: TraineeDetails) : this(
            userDetails.name,
            userDetails.email,
            trainee.gender,
            trainee.birthdate,
            trainee.phoneNumber
        )
    }

    data class TrainerUserDetails(
        val name: String,
        val email: String,
        val gender: Gender,
        val phoneNumber: String?
    ) : UserDetailsResponse() {
        constructor(userDetails: UserDetails, trainer: TrainerDetails) : this(
            userDetails.name,
            userDetails.email,
            trainer.gender,
            trainer.phoneNumber
        )
    }

    data class CompanyUserDetails(
        val name: String,
        val email: String
    ) : UserDetailsResponse() {
        constructor(company: UserDetails) : this(company.name, company.email)
    }
}
