package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.workout.ExerciseDetails
import java.util.*

interface TrainerRepo {

    fun getCompanyAssignedTrainer(trainerId: UUID): UUID

    fun getExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails?

    fun associateTrainerToExercise(trainerId: UUID, exerciseId: Int)

    fun associateTrainerToSet(trainerId: UUID, setId: Int)

    fun getLastSetNameId(trainerId: UUID): Int
}
