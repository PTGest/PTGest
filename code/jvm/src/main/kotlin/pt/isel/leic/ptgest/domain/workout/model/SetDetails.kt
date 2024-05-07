package pt.isel.leic.ptgest.domain.workout.model

import pt.isel.leic.ptgest.domain.workout.SetType

data class SetDetails(
    val name: String,
    val notes: String?,
    val type: SetType,
    val exercises: List<Exercise>
) {
    constructor(set: Set, exercises: List<Exercise>) : this(set.name, set.notes, set.type, exercises)
}
