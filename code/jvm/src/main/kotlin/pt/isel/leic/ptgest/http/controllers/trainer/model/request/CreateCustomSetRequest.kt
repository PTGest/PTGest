package pt.isel.leic.ptgest.http.controllers.trainer.model.request

import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.SetExercise

data class CreateCustomSetRequest(
    val name: String?,
    val notes: String?,
    val type: SetType,
    val setExercises: List<SetExercise>
)
