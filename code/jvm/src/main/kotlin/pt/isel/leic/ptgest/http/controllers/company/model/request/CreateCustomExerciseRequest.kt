package pt.isel.leic.ptgest.http.controllers.company.model.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class CreateCustomExerciseRequest(

    @NotEmpty
    val name: String,

    val description: String?,

    @field:Size(max = 3)
    val muscleGroup: List<MuscleGroup>,

    val modality: Modality,

    val ref: String?
)
