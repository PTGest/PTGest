package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.SetType

data class SetDetails(
    val name: String,
    val notes: String?,
    val type: SetType,
    val setExerciseDetails: List<SetExerciseDetails>
) {
    constructor(set: Set, setExerciseDetails: List<SetExerciseDetails>) : this(set.name, set.notes, set.type, setExerciseDetails)
}
