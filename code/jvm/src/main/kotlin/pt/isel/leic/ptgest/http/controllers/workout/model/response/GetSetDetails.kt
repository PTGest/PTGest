package pt.isel.leic.ptgest.http.controllers.workout.model.response

import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.SetExerciseDetails

data class GetSetDetails(
    val name: String,
    val notes: String?,
    val type: SetType,
    val setExerciseDetails: List<SetExerciseDetails>
) {
    constructor(setDetails: SetDetails) :
        this(setDetails.name, setDetails.notes, setDetails.type, setDetails.setExerciseDetails)
}
