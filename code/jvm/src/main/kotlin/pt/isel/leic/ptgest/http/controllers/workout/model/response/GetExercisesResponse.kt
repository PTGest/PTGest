package pt.isel.leic.ptgest.http.controllers.workout.model.response

import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.Exercises

data class GetExercisesResponse(
    val exercises: List<Exercise>,
    val total: Int
) {
    constructor(exercises: Exercises) : this(exercises.exercises, exercises.total)
}
