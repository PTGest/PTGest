package pt.isel.leic.ptgest.http.controllers.model.response

import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails

data class GetExerciseDetailsResponse(
    val name: String,
    val description: String?,
    val muscleGroup: MuscleGroup,
    val type: Modality,
    val ref: String?
) {
    constructor(exercise: ExerciseDetails) :
        this(exercise.name, exercise.description, exercise.muscleGroup, exercise.modality, exercise.ref)
}
