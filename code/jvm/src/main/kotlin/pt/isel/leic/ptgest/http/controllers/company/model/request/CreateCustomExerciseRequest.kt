package pt.isel.leic.ptgest.http.controllers.company.model.request

import jakarta.validation.constraints.NotEmpty
import pt.isel.leic.ptgest.domain.workout.ExerciseType
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class CreateCustomExerciseRequest(

    @NotEmpty
    val name: String,

    val description: String?,

    val muscleGroup: MuscleGroup,

    val exerciseType: ExerciseType,

    val ref: String?
)
