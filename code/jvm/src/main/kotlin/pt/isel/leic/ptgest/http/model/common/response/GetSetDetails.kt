package pt.isel.leic.ptgest.http.model.common.response

import pt.isel.leic.ptgest.domain.set.model.SetDetails
import pt.isel.leic.ptgest.domain.set.model.SetExerciseDetails
import pt.isel.leic.ptgest.domain.workout.SetType

data class GetSetDetails(
    val name: String,
    val notes: String?,
    val type: SetType,
    val setExerciseDetails: List<SetExerciseDetails>
) {
    constructor(setDetails: SetDetails) :
        this(setDetails.name, setDetails.notes, setDetails.type, setDetails.setExerciseDetails)
}
