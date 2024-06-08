package pt.isel.leic.ptgest.http.model.trainer.request

import jakarta.validation.constraints.Size
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class CreateWorkoutRequest(
    val name: String?,
    val description: String?,
    @field:Size(min = 1, max = 3, message = "Muscle group list must have at least one and at most three muscle groups.")
    val muscleGroup: List<MuscleGroup>,
    @field:Size(min = 1, message = "Workout must have at least one set.")
    val sets: List<Int>
) {
    init {
        name?.trim()
        description?.trim()
    }
}
