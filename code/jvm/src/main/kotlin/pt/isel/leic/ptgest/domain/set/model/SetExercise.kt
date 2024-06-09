package pt.isel.leic.ptgest.domain.set.model

import pt.isel.leic.ptgest.domain.set.ExerciseDetailsType

data class SetExercise(
    val exerciseId: Int,
    val details: Map<ExerciseDetailsType, String>
)
