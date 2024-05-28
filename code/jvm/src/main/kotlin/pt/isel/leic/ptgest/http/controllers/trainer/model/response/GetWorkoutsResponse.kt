package pt.isel.leic.ptgest.http.controllers.trainer.model.response

import pt.isel.leic.ptgest.domain.workout.model.Workout

data class GetWorkoutsResponse(
    val workouts: List<Workout>,
    val total: Int
)
