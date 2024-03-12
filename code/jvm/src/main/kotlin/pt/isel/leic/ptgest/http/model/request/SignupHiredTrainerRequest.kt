package pt.isel.leic.ptgest.http.model.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import pt.isel.leic.ptgest.domain.common.Gender

data class SignupHiredTrainerRequest (
    @NotEmpty
    val name: String,

    @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    val email: String,

    val gender: Gender,

    @field:Pattern(regexp = "^\\+?(\\d[\\d-. ]+)?(\\([\\d-. ]+\\))?[\\d-. ]+\\d\$")
    val phoneNumber: String? //can be optional
)
