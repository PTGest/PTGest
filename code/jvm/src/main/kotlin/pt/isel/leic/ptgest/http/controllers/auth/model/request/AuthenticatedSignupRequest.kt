package pt.isel.leic.ptgest.http.controllers.auth.model.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import pt.isel.leic.ptgest.domain.common.Gender
import java.util.Date

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "user_type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AuthenticatedSignupRequest.HiredTrainer::class, name = "hired_trainer"),
    JsonSubTypes.Type(value = AuthenticatedSignupRequest.Trainee::class, name = "trainee")
)
sealed class AuthenticatedSignupRequest {

    @JsonTypeName("hired_trainer")
    data class HiredTrainer(
        @NotEmpty
        val name: String,

        @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        val email: String,

        val gender: Gender,

        @field:Pattern(regexp = "^\\+?(\\d[\\d-. ]+)?(\\([\\d-. ]+\\))?[\\d-. ]+\\d\$")
        val phoneNumber: String? // can be optional
    ) : AuthenticatedSignupRequest()

    @JsonTypeName("trainee")
    data class Trainee(
        @NotEmpty
        val name: String,

        @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        val email: String,

        val birthdate: Date,

        val gender: Gender,

        @field:Pattern(regexp = "^\\+?(\\d[\\d-. ]+)?(\\([\\d-. ]+\\))?[\\d-. ]+\\d\$")
        val phoneNumber: String?
    ) : AuthenticatedSignupRequest()
}
