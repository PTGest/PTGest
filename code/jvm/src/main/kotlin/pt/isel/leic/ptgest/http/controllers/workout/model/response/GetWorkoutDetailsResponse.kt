package pt.isel.leic.ptgest.http.controllers.workout.model.response

import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails

data class GetWorkoutDetailsResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val muscleGroup: List<MuscleGroup>,
    val sets: List<SetDetails>
) {
    constructor(workoutDetails: WorkoutDetails) :
        this(workoutDetails.id, workoutDetails.name, workoutDetails.description, workoutDetails.muscleGroup, workoutDetails.sets)
}
