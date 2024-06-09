package pt.isel.leic.ptgest.http.model.trainer.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import pt.isel.leic.ptgest.domain.set.model.SetExercise
import pt.isel.leic.ptgest.domain.workout.SetType

data class EditSetRequest(
    @field:NotEmpty(message = "Set name must not be empty.")
    val name: String,
    val notes: String?,
    val type: SetType,
    @field:Size(min = 1, message = "Set must have at least one exercise.")
    val setExercises: List<SetExercise>
) {
    init {
        name.trim()
        notes?.trim()
    }
}
