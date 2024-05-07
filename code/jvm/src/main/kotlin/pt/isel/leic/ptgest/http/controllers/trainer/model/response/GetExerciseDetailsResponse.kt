package pt.isel.leic.ptgest.http.controllers.trainer.model.response

import pt.isel.leic.ptgest.domain.workout.ExerciseType
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails

data class GetExerciseDetailsResponse(
    val name: String,
    val description: String?,
    val muscleGroup: MuscleGroup,
    val type: ExerciseType,
    val ref: String?
) {
    constructor(exercise: ExerciseDetails) :
        this(exercise.name, exercise.description, exercise.muscleGroup, exercise.type, exercise.ref)
}
