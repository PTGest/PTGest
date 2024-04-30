package pt.isel.leic.ptgest.http.controllers.trainer.model.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import pt.isel.leic.ptgest.domain.common.SetDetails

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "setType"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateCustomSetRequest.DropSet::class, name = "DROPSET"),
    JsonSubTypes.Type(value = CreateCustomSetRequest.SuperSet::class, name = "SUPERSET"),
    JsonSubTypes.Type(value = CreateCustomSetRequest.Running::class, name = "RUNNING"),
    JsonSubTypes.Type(value = CreateCustomSetRequest.BodyWeight::class, name = "BODYWEIGHT"),
    JsonSubTypes.Type(value = CreateCustomSetRequest.WeightedLift::class, name = "WEIGHTEDLIFT")
)
sealed class CreateCustomSetRequest {
    abstract val name: String?
    abstract val notes: String?
    abstract val details: SetDetails

    @JsonTypeName("DROPSET")
    data class DropSet(
        override val name: String?,
        override val notes: String?,
        override val details: SetDetails.DropSet
    ) : CreateCustomSetRequest()

    @JsonTypeName("SUPERSET")
    data class SuperSet(
        override val name: String?,
        override val notes: String?,
        override val details: SetDetails.SuperSet
    ) : CreateCustomSetRequest()

    @JsonTypeName("RUNNING")
    data class Running(
        override val name: String?,
        override val notes: String?,
        override val details: SetDetails.Running
    ) : CreateCustomSetRequest()

    @JsonTypeName("BODYWEIGHT")
    data class BodyWeight(
        override val name: String?,
        override val notes: String?,
        override val details: SetDetails.BodyWeight
    ) : CreateCustomSetRequest()

    @JsonTypeName("WEIGHTEDLIFT")
    data class WeightedLift(
        override val name: String?,
        override val notes: String?,
        override val details: SetDetails.WeightedLift
    ) : CreateCustomSetRequest()
}
