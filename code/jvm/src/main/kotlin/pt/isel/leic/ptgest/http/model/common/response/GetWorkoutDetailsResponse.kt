package pt.isel.leic.ptgest.http.model.common.response

import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutSetDetails

data class GetWorkoutDetailsResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val muscleGroup: List<MuscleGroup>,
    val sets: List<WorkoutSetDetails>
) {
    constructor(workoutDetails: WorkoutDetails) :
        this(
            workoutDetails.id,
            workoutDetails.name,
            workoutDetails.description,
            workoutDetails.muscleGroup,
            workoutDetails.sets
        )
}
