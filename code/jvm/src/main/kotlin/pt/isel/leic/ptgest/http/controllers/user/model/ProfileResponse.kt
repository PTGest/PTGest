package pt.isel.leic.ptgest.http.controllers.user.model

import pt.isel.leic.ptgest.domain.auth.model.UserDetails
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.user.TraineeDetails
import pt.isel.leic.ptgest.domain.user.TrainerDetails
import java.util.*

sealed class ProfileResponse {

    data class TraineeProfile(
        val name: String,
        val email: String,
        val gender: Gender,
        val birthdate: Date,
        val phoneNumber: String?
    ) : ProfileResponse()

    data class TrainerProfile(
        val name: String,
        val email: String,
        val gender: Gender,
        val phoneNumber: String?
    ) : ProfileResponse()

    data class CompanyProfile(
        val name: String,
        val email: String
    ) : ProfileResponse()

    companion object {
        fun TraineeDetails.toResponse() = TraineeProfile(
            name = name,
            email = email,
            gender = gender,
            birthdate = birthdate,
            phoneNumber = phoneNumber
        )

        fun TrainerDetails.toResponse() = TrainerProfile(
            name = name,
            email = email,
            gender = gender,
            phoneNumber = phoneNumber
        )

        fun UserDetails.toResponse() = CompanyProfile(
            name = name,
            email = email
        )
    }
}
