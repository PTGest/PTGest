package pt.isel.leic.ptgest.services.trainee

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
import pt.isel.leic.ptgest.domain.session.model.SessionDetails
import pt.isel.leic.ptgest.domain.session.model.SessionFeedback
import pt.isel.leic.ptgest.domain.session.model.SetSessionFeedback
import pt.isel.leic.ptgest.domain.set.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutSetDetails
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
                val set = setRepo.getWorkoutSet(workoutId, setId) ?: return@mapNotNull null
                val exercises = setRepo.getSetExercises(setId)
                WorkoutSetDetails(set, exercises)
            }

            return@run WorkoutDetails(workout, sets)
        }

    fun getSessions(
        traineeId: UUID,
        sessionType: SessionType?,
        date: Date?,
        skip: Int?,
        limit: Int?
    ): Pair<List<Session>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 }
        )

        return transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessions = sessionRepo.getTraineeSessions(traineeId, sessionType, date, skip ?: 0, limit)
            val total = sessionRepo.getTotalTraineeSessions(traineeId, sessionType, date)

            return@run Pair(sessions, total)
        }
    }

    fun getSessionDetails(
        traineeId: UUID,
        sessionId: Int
    ): Pair<SessionDetails, List<SessionFeedback>> =
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessionDetails = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            val feedbacks = sessionRepo.getSessionFeedbacks(sessionId)

            return@run Pair(sessionDetails, feedbacks)
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

    fun createSessionFeedback(
        traineeId: UUID,
        sessionId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            require(requestDate.after(session.beginDate)) { "Feedback can be created only after the session begin date." }

            sessionRepo.createFeedback(Source.TRAINEE, feedback, requestDate).also { feedbackId ->
                sessionRepo.createSessionFeedback(feedbackId, sessionId)
            }
        }
    }

    fun editSessionFeedback(
        traineeId: UUID,
        sessionId: Int,
        feedbackId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            require(requestDate.after(session.beginDate)) { "Feedback can be edited only after the session begin date." }

            sessionRepo.getSessionFeedback(feedbackId, sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            sessionRepo.editFeedback(feedbackId, feedback, requestDate)
        }
    }

    fun createSessionSetFeedback(
        traineeId: UUID,
        sessionId: Int,
        setOrderId: Int,
        setId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            require(requestDate.after(session.beginDate)) { "Feedback can be created only after the session begin date." }

            sessionRepo.createFeedback(Source.TRAINEE, feedback, requestDate).also { feedbackId ->
                sessionRepo.createSessionSetFeedback(feedbackId, sessionId, session.workoutId, setOrderId, setId)
            }
        }
    }

    fun editSessionSetFeedback(
        traineeId: UUID,
        sessionId: Int,
        feedbackId: Int,
        setOrderId: Int,
        setId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            require(requestDate.after(session.beginDate)) { "Feedback can be edited only after the session begin date." }
            require(sessionRepo.validateSessionSet(sessionId, setOrderId, setId)) { "Set must be part of the session." }

            sessionRepo.getSetSessionFeedback(feedbackId, sessionId, session.workoutId, setOrderId, setId)
                ?: throw TrainerError.ResourceNotFoundError

            sessionRepo.editFeedback(feedbackId, feedback, requestDate)
        }
    }

    fun getSetSessionFeedbacks(
        traineeId: UUID,
        sessionId: Int
    ): List<SetSessionFeedback> =
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            sessionRepo.getSessionDetails(traineeId, sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            sessionRepo.getSetSessionFeedbacks(sessionId)
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
