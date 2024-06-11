package pt.isel.leic.ptgest.services.trainee

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
import pt.isel.leic.ptgest.domain.set.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.trainer.TrainerError
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.*

@Service
class TraineeService(
    private val transactionManager: TransactionManager
) {

    fun getExerciseDetails(
        traineeId: UUID,
        exerciseId: Int
    ): ExerciseDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val traineeRepo = it.traineeRepo
            val exerciseRepo = it.exerciseRepo

            val trainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssignedError
            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            exerciseRepo.getTrainerExerciseDetails(traineeId, exerciseId)
                ?: companyId?.let { exerciseRepo.getCompanyExerciseDetails(companyId, exerciseId) }
                ?: throw TraineeError.ExerciseNotFoundError
        }

    fun getSetDetails(
        traineeId: UUID,
        setId: Int
    ): SetDetails =
        transactionManager.run {
            val traineeRepo = it.traineeRepo
            val setRepo = it.setRepo

            val trainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssignedError

            val set = setRepo.getSet(trainerId, setId)
                ?: throw TraineeError.SetNotFoundError
            val exercises = setRepo.getSetExercises(setId)

            return@run SetDetails(set, exercises)
        }

    fun getWorkoutDetails(
        traineeId: UUID,
        workoutId: Int
    ): WorkoutDetails =
        transactionManager.run {
            val traineeRepo = it.traineeRepo
            val workoutRepo = it.workoutRepo
            val setRepo = it.setRepo

            val trainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssignedError

            val workout = workoutRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TraineeError.WorkoutNotFoundError

            val sets = workoutRepo.getWorkoutSetIds(workoutId).mapNotNull { setId ->
                val set = setRepo.getSet(trainerId, setId) ?: return@mapNotNull null
                val exercises = setRepo.getSetExercises(setId)
                SetDetails(set, exercises)
            }

            return@run WorkoutDetails(workout, sets)
        }

    fun getSessions(
        traineeId: UUID,
        skip: Int?,
        limit: Int?,
        sessionType: SessionType?
    ): Pair<List<Session>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 }
        )

        return transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessions = sessionRepo.getTraineeSessions(traineeId, skip ?: 0, limit, sessionType)
            val total = sessionRepo.getTotalTraineeSessions(traineeId, sessionType)

            return@run Pair(sessions, total)
        }
    }

    fun getSessionDetails(
        traineeId: UUID,
        sessionId: Int
    ): Session =
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            return@run sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw TrainerError.ResourceNotFoundError
        }

    fun cancelSession(
        traineeId: UUID,
        sessionId: Int,
        reason: String?
    ) {
        Validators.validate(
            Validators.ValidationRequest(reason, "Reason must be a non-empty string.") { it is String && it.isNotBlank() }
        )
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            require(isCurrentDate24BeforeDate(session.beginDate)) { "Session can be canceled only 24 hours before the begin date." }

            sessionRepo.cancelSession(sessionId, Source.TRAINEE, reason)
        }
    }

    private fun isCurrentDate24BeforeDate(date: Date): Boolean {
        val calendar = Calendar.getInstance()

        val currentDate = calendar.time

        calendar.time = date
        calendar.add(Calendar.HOUR, -24)
        val date24HoursBefore = calendar.time

        return currentDate.before(date24HoursBefore)
    }
}
