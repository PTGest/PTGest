package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class WorkoutDetails(
    val name: String,
    val description: String?,
    val type: MuscleGroup,
    val sets: List<SetDetails>
) {
    constructor(workout: Workout, sets: List<SetDetails>) :
        this(workout.name, workout.description, workout.type, sets)
}
