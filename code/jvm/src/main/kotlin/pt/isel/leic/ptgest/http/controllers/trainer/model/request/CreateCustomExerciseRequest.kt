package pt.isel.leic.ptgest.http.controllers.trainer.model.request

import jakarta.validation.constraints.NotEmpty
import pt.isel.leic.ptgest.domain.common.ExerciseType

data class CreateCustomExerciseRequest(

    @NotEmpty
    val name: String,

    val description: String?,

    val category: ExerciseType,

    val ref: String?
)
