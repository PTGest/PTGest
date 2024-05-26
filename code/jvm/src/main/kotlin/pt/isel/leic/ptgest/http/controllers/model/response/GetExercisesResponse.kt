package pt.isel.leic.ptgest.http.controllers.model.response

import pt.isel.leic.ptgest.domain.workout.model.Exercise

data class GetExercisesResponse(
    val exercises: List<Exercise>,
    val total: Int
)
