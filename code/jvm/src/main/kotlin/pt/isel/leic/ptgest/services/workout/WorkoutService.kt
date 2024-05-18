package pt.isel.leic.ptgest.services.workout

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.SetExercise
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.UUID

@Service
class WorkoutService(
    private val transactionManager: TransactionManager
) {
    fun createCustomExercise(
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        modality: Modality,
        ref: String?
    ): Int {
        Validators.validate(
            Validators.ValidationRequest(description, "Description must not be empty.") { (it as String).isNotEmpty() },
            Validators.ValidationRequest(ref, "Reference must be a valid YouTube URL.") { isYoutubeUrl(it as String) }
        )

        return transactionManager.run {
            val workoutRepo = it.workoutRepo

            return@run workoutRepo.createExercise(
                name.trim(),
                description,
                muscleGroup,
                modality,
                ref
            )
        }
    }

    fun createCustomSet(
        trainerId: UUID,
        userRole: Role,
        name: String?,
        notes: String?,
        setType: SetType,
        setExercises: List<SetExercise>
    ): Int {
        if (notes != null) {
            require(notes.isNotEmpty()) { "Notes must not be empty." }
        }

        val setId = transactionManager.runWithLevel(TransactionIsolationLevel.SERIALIZABLE) {
            it.createSet(trainerId, name, notes, setType)
        }

        transactionManager.run {
            val workoutRepo = it.workoutRepo

            setExercises.forEachIndexed { index, set ->
                val exercise = it.getExercise(trainerId, userRole, set.exerciseId)

                val validator = ExerciseValidator.getValidator(setType, exercise.modality)
                val validatedDetails = validator.validate(set.details)

                val jsonDetails = convertDataToJson(validatedDetails)

                workoutRepo.associateExerciseToSet(index + 1, set.exerciseId, setId, jsonDetails)
            }
        }
        return setId
    }

    fun createCustomWorkout(
        trainerId: UUID,
        name: String?,
        description: String?,
        category: MuscleGroup,
        sets: List<Int>
    ): Int {
        if (description != null) {
            require(description.isNotEmpty()) { "Description must not be empty." }
        }

        val workoutId = transactionManager.runWithLevel(TransactionIsolationLevel.SERIALIZABLE) {
            it.createWorkout(trainerId, name, description, category)
        }

        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val trainerRepo = it.trainerRepo

            sets.forEachIndexed { index, setId ->
                trainerRepo.getSet(trainerId, setId)
                    ?: throw WorkoutError.SetNotFoundError

                workoutRepo.associateSetToWorkout(index + 1, setId, workoutId)
            }
        }

        return workoutId
    }

    private fun convertDataToJson(data: Map<String, Any>): String {
        val jsonMapper = jacksonObjectMapper()

        return jsonMapper.writeValueAsString(data)
    }

    private fun isYoutubeUrl(url: String): Boolean {
        val pattern = "^(https://)?((w){3}.)?youtube\\.com/watch\\?v=\\w+"
        val compiledPattern = Regex(pattern)
        return compiledPattern.matches(url)
    }

    private fun Transaction.createSet(
        trainerId: UUID,
        name: String?,
        notes: String?,
        setType: SetType
    ): Int =
        if (name != null) {
            require(name.isNotEmpty()) { "Name must not be empty." }
            workoutRepo.createSet(trainerId, name.trim(), notes, setType)
        } else {
            val lastSetNameId = trainerRepo.getLastSetNameId(trainerId)
            val nextSetName = "Set #${lastSetNameId + 1}"
            workoutRepo.createSet(trainerId, nextSetName, notes, setType)
        }

    private fun Transaction.getExercise(userId: UUID, userRole: Role, exerciseId: Int): ExerciseDetails =
        when (userRole) {
            Role.INDEPENDENT_TRAINER -> {
                trainerRepo.getExerciseDetails(userId, exerciseId)
                    ?: throw WorkoutError.ExerciseNotFoundError
            }

            Role.HIRED_TRAINER -> {
                val companyId = trainerRepo.getCompanyAssignedTrainer(userId)
                trainerRepo.getExerciseDetails(userId, exerciseId)
                    ?: companyRepo.getExerciseDetails(companyId, exerciseId)
                    ?: throw WorkoutError.ExerciseNotFoundError
            }

            else -> throw AuthError.UserAuthenticationError.UnauthorizedRole
        }

    private fun Transaction.createWorkout(
        trainerId: UUID,
        name: String?,
        description: String?,
        category: MuscleGroup
    ): Int =
        if (name != null) {
            require(name.isNotEmpty()) { "Name must not be empty." }
            workoutRepo.createWorkout(trainerId, name.trim(), description, category)
        } else {
            val lastWorkoutNameId = trainerRepo.getLastWorkoutNameId(trainerId)
            val nextWorkoutName = "Workout #${lastWorkoutNameId + 1}"
            workoutRepo.createWorkout(trainerId, nextWorkoutName, description, category)
        }
}
