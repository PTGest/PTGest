package pt.isel.leic.ptgest.http.controllers.user.model

import pt.isel.leic.ptgest.domain.common.Gender
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
}
