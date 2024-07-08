package pt.isel.leic.ptgest.http.model.auth.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import pt.isel.leic.ptgest.domain.user.Gender

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "user_type"
)
@JsonSubTypes(
    Type(value = SignupRequest.Company::class, name = "company"),
    Type(value = SignupRequest.IndependentTrainer::class, name = "independent_trainer")
)
sealed class SignupRequest {

    @JsonTypeName("company")
    data class Company(
        @field:NotEmpty(message = "Name cannot be empty.")
        val name: String,

        @field:NotEmpty(message = "Email cannot be empty.")
        @field:Email(message = "Invalid email address.")
        val email: String,

        @field:Size(min = 8, message = "Password must have at least 8 characters.")
        val password: String
    ) : SignupRequest() {
        init {
            name.trim()
            email.trim()
            password.trim()
        }
    }

    @JsonTypeName("independent_trainer")
    data class IndependentTrainer(
        @field:NotEmpty(message = "Name cannot be empty.")
        val name: String,

        @field:NotEmpty(message = "Email cannot be empty.")
        @field:Email(message = "Invalid email address.")
        val email: String,

        @field:Size(min = 8, message = "Password must have at least 8 characters.")
        val password: String,

        val gender: Gender,

        @field:NotEmpty(message = "Phone number cannot be empty.")
        @field:Pattern(
            regexp = "^\\+?(\\d[\\d-. ]+)?(\\([\\d-. ]+\\))?[\\d-. ]+\\d\$",
            message = "Invalid phone number."
        )
        val phoneNumber: String?
    ) : SignupRequest() {
        init {
            name.trim()
            email.trim()
            password.trim()
            phoneNumber?.trim()
        }
    }
}
