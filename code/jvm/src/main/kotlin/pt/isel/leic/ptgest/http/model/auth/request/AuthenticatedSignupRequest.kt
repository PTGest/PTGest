package pt.isel.leic.ptgest.http.model.auth.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import pt.isel.leic.ptgest.domain.user.Gender
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
        @field:NotEmpty(message = "Name cannot be empty.")
        val name: String,

        @field:NotEmpty(message = "Email cannot be empty.")
        @field:Email(message = "Invalid email address.")
        val email: String,

        val gender: Gender,

        val capacity: Int,

        @field:NotEmpty(message = "Phone Number cannot be empty.")
        @field:Pattern(
            regexp = "^\\+?(\\d[\\d-. ]+)?(\\([\\d-. ]+\\))?[\\d-. ]+\\d\$",
            message = "Invalid phone number."
        )
        val phoneNumber: String?
    ) : AuthenticatedSignupRequest() {
        init {
            name.trim()
            email.trim()
            phoneNumber?.trim()
        }
    }

    @JsonTypeName("trainee")
    data class Trainee(
        @field:NotEmpty(message = "Name cannot be empty.")
        val name: String,

        @field:NotEmpty(message = "Email cannot be empty.")
        @field:Email(message = "Invalid email address.")
        val email: String,

        val birthdate: Date,

        val gender: Gender,

        @field:NotEmpty(message = "Phone Number cannot be empty.")
        @field:Pattern(
            regexp = "^\\+?(\\d[\\d-. ]+)?(\\([\\d-. ]+\\))?[\\d-. ]+\\d\$",
            message = "Invalid phone number."
        )
        val phoneNumber: String?
    ) : AuthenticatedSignupRequest() {
        init {
            name.trim()
            email.trim()
            phoneNumber?.trim()
        }
    }
}
