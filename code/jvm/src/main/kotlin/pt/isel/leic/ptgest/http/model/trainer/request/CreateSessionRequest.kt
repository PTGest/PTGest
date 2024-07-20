package pt.isel.leic.ptgest.http.model.trainer.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import jakarta.validation.constraints.NotEmpty
import java.util.Date
import java.util.UUID

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateSessionRequest.TrainerGuided::class, name = "trainer_guided"),
    JsonSubTypes.Type(value = CreateSessionRequest.PlanBased::class, name = "plan_based")
)
sealed class CreateSessionRequest {
    @JsonTypeName("trainer_guided")
    data class TrainerGuided(
        val traineeId: UUID,
        val workoutId: Int,
        val beginDate: Date,
        val endDate: Date,
        @field:NotEmpty(message = "Name cannot be empty.")
        val location: String,
        val notes: String?
    ) : CreateSessionRequest()

    @JsonTypeName("plan_based")
    data class PlanBased(
        val traineeId: UUID,
        val workoutId: Int,
        val beginDate: Date,
        val notes: String?
    ) : CreateSessionRequest()
}
