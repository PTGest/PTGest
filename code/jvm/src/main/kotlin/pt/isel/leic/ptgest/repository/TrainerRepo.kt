package pt.isel.leic.ptgest.repository

import java.util.*

interface TrainerRepo {

    fun associateTrainerToExercise(exerciseId: Int, trainerId: UUID)

    fun associateTrainerToSet(setId: Int, trainerId: UUID)
}
