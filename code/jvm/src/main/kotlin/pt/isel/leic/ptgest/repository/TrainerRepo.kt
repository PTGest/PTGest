package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.trainee.model.Trainee
import pt.isel.leic.ptgest.domain.trainer.model.TrainerDetails
import pt.isel.leic.ptgest.domain.user.Gender
import java.util.*

interface TrainerRepo {

    fun getTrainees(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        gender: Gender?,
        name: String?
    ): List<Trainee>

    fun getTotalTrainees(trainerId: UUID, gender: Gender?, name: String?): Int

    fun getTrainerDetails(trainerId: UUID): TrainerDetails?

    fun getCompanyAssignedTrainer(trainerId: UUID): UUID?

    fun associateTrainerToReport(trainerId: UUID, reportId: Int)

    fun associateTrainerToExercise(trainerId: UUID, exerciseId: Int)

    fun associateTrainerToSet(trainerId: UUID, setId: Int)

    fun associateTrainerToWorkout(trainerId: UUID, workoutId: Int)

    fun associateSessionToTrainer(trainerId: UUID, sessionId: Int)
}
