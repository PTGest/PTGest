package pt.isel.leic.ptgest.http.controllers.trainer.model.response

import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails

data class GetWorkoutDetailsResponse(
    val name: String,
    val description: String?,
    val type: MuscleGroup,
    val sets: List<SetDetails>
) {
    constructor(workoutDetails: WorkoutDetails) :
        this(workoutDetails.name, workoutDetails.description, workoutDetails.type, workoutDetails.sets)
}
