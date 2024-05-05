package pt.isel.leic.ptgest.http.controllers.company.model.request

import jakarta.validation.constraints.NotEmpty
import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.MuscleGroup

data class CreateCustomExerciseRequest(

    @NotEmpty
    val name: String,

    val description: String?,

    val muscleGroup: MuscleGroup,

    val exerciseType: ExerciseType,

    val ref: String?
)
