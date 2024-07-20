package pt.isel.leic.ptgest.http.model.trainer.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import jakarta.validation.constraints.NotEmpty
import java.util.*

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = EditSessionRequest.TrainerGuided::class, name = "trainer_guided"),
    JsonSubTypes.Type(value = EditSessionRequest.PlanBased::class, name = "plan_based")
)
sealed class EditSessionRequest {
    @JsonTypeName("trainer_guided")
    data class TrainerGuided(
        val workoutId: Int,
        val beginDate: Date,
        val endDate: Date,
        @field:NotEmpty(message = "Location cannot be empty.")
        val location: String,
        val notes: String?
    ) : EditSessionRequest()

    @JsonTypeName("plan_based")
    data class PlanBased(
        val workoutId: Int,
        val beginDate: Date,
        val notes: String?
    ) : EditSessionRequest()
}
