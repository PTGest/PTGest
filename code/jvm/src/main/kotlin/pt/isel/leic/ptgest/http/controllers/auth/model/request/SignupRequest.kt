package pt.isel.leic.ptgest.http.controllers.auth.model.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import pt.isel.leic.ptgest.domain.common.Gender

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
        @NotEmpty
        val name: String,

        @NotEmpty
        @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        val email: String,

        @field:Size(min = 8)
        val password: String
    ) : SignupRequest()

    @JsonTypeName("independent_trainer")
    data class IndependentTrainer(
        @NotEmpty
        val name: String,

        @NotEmpty
        @field:Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        val email: String,

        @field:Size(min = 8)
        val password: String,

        val gender: Gender,

        @field:Pattern(regexp = "^\\+?(\\d[\\d-. ]+)?(\\([\\d-. ]+\\))?[\\d-. ]+\\d\$")
        val phoneNumber: String?
    ) : SignupRequest()
}