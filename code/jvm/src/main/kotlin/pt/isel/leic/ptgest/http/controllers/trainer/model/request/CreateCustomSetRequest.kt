package pt.isel.leic.ptgest.http.controllers.trainer.model.request

import jakarta.validation.constraints.NotEmpty
import pt.isel.leic.ptgest.domain.common.SetDetails
import pt.isel.leic.ptgest.domain.common.SetType

data class CreateCustomSetRequest(

    @NotEmpty
    val name: String,

    val notes: String?,

    val setType: SetType,

    val sets: List<SetDetails>
)
