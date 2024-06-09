package pt.isel.leic.ptgest.http.model.common.response

import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup

data class GetExerciseDetailsResponse(
    val name: String,
    val description: String?,
    val muscleGroup: List<MuscleGroup>,
    val type: Modality,
    val ref: String?
) {
    constructor(exercise: ExerciseDetails) :
        this(exercise.name, exercise.description, exercise.muscleGroup, exercise.modality, exercise.ref)
}
