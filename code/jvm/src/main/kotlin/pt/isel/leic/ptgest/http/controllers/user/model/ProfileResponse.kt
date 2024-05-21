package pt.isel.leic.ptgest.http.controllers.user.model

import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.user.model.TraineeDetails
import pt.isel.leic.ptgest.domain.user.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.model.UserDetails
import java.util.Date

sealed class ProfileResponse {

    data class TraineeProfile(
        val name: String,
        val email: String,
        val gender: Gender,
        val birthdate: Date,
        val phoneNumber: String?
    ) : ProfileResponse() {
        constructor(trainee: TraineeDetails) : this(
            trainee.name,
            trainee.email,
            trainee.gender,
            trainee.birthdate,
            trainee.phoneNumber
        )
    }

    data class TrainerProfile(
        val name: String,
        val email: String,
        val gender: Gender,
        val phoneNumber: String?
    ) : ProfileResponse() {
        constructor(trainer: TrainerDetails) : this(
            trainer.name,
            trainer.email,
            trainer.gender,
            trainer.phoneNumber
        )
    }

    data class CompanyProfile(
        val name: String,
        val email: String
    ) : ProfileResponse() {
        constructor(company: UserDetails) : this(company.name, company.email)
    }
}
