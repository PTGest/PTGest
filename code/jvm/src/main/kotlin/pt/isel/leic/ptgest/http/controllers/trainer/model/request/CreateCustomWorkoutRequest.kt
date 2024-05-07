package pt.isel.leic.ptgest.http.controllers.trainer.model.request

import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class CreateCustomWorkoutRequest(
    val name: String?,
    val description: String?,
    val category: MuscleGroup,
    val sets: List<Int>
)
