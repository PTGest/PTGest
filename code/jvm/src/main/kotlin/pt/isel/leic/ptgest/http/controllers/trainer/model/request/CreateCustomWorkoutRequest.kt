package pt.isel.leic.ptgest.http.controllers.trainer.model.request

import jakarta.validation.constraints.Size
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class CreateCustomWorkoutRequest(
    val name: String?,
    val description: String?,
    @field:Size(max = 3, message = "Muscle group list must have at most 3 elements.")
    val muscleGroup: List<MuscleGroup>,
    val sets: List<Int>
) {
    init {
        name?.trim()
        description?.trim()
    }
}
