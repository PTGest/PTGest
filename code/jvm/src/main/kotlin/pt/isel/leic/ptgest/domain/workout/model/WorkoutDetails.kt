package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class WorkoutDetails(
    val id: Int,
    val name: String,
    val description: String?,
    val muscleGroup: List<MuscleGroup>,
    val sets: List<SetDetails>
) {
    constructor(workout: Workout, sets: List<SetDetails>) :
        this(workout.id, workout.name, workout.description, workout.muscleGroup, sets)
}
