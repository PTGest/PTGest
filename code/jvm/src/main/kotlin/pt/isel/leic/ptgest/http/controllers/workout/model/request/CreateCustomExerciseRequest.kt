package pt.isel.leic.ptgest.http.controllers.workout.model.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class CreateCustomExerciseRequest(
    @field:NotEmpty(message = "Name cannot be empty")
    val name: String,
    val description: String?,
    @field:Size(max = 3, message = "Muscle group list cannot have more than 3 elements")
    val muscleGroup: List<MuscleGroup>,
    val modality: Modality,
    val ref: String?
) {
    init {
        name.trim()
        description?.trim()
        ref?.trim()
    }
}
