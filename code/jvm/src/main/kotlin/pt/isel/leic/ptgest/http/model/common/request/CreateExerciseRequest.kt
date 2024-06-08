package pt.isel.leic.ptgest.http.model.common.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class CreateExerciseRequest(
    @field:NotEmpty(message = "Name cannot be empty")
    val name: String,
    val description: String?,
    @field:Size(min = 1, max = 3, message = "Muscle group must have between 1 and 3 elements")
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
