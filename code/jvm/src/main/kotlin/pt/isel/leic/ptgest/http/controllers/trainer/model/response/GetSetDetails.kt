package pt.isel.leic.ptgest.http.controllers.trainer.model.response

import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.SetDetails

data class GetSetDetails(
    val name: String,
    val notes: String?,
    val type: SetType,
    val exercises: List<Exercise>
) {
    constructor(setDetails: SetDetails) :
        this(setDetails.name, setDetails.notes, setDetails.type, setDetails.exercises)
}
