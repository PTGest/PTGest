package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.set.model.SetExerciseDetails
import pt.isel.leic.ptgest.domain.workout.SetType

data class WorkoutSetDetails(
    val id: Int,
    val orderId: Int,
    val name: String,
    val notes: String?,
    val type: SetType,
    val setExerciseDetails: List<SetExerciseDetails>
) {
    constructor(workoutSet: WorkoutSet, setExerciseDetails: List<SetExerciseDetails>) :
        this(workoutSet.id, workoutSet.orderId, workoutSet.name, workoutSet.notes, workoutSet.type, setExerciseDetails)
}
