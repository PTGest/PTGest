package pt.isel.leic.ptgest.services.workout

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.MuscleGroup
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.domain.common.SetDetails
import pt.isel.leic.ptgest.domain.common.SetType
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.auth.AuthError
import java.util.UUID

@Service
class WorkoutService(
    private val transactionManager: TransactionManager
) {
    fun createCustomExercise(
        name: String,
        description: String?,
        muscleGroup: MuscleGroup,
        exerciseType: ExerciseType,
        ref: String?
    ): Int =
        transactionManager.run {
            val workoutRepo = it.workoutRepo

            return@run workoutRepo.createExercise(
                name,
                description,
                muscleGroup,
                exerciseType,
                ref
            )
        }

    fun createCustomSet(
        userId: UUID,
        userRole: Role,
        name: String?,
        notes: String?,
        setType: SetType,
        sets: List<SetDetails>
    ): Int {
        if (notes != null) {
            require(notes.isNotEmpty()) { "Notes must not be empty." }
        }

        val setId = transactionManager.runWithLevel(TransactionIsolationLevel.SERIALIZABLE) {
            it.createSet(userId, name, notes, setType)
        }

        transactionManager.run {
            val workoutRepo = it.workoutRepo

            sets.forEachIndexed { index, set ->
                it.validateExercise(userId, userRole, set.exerciseId)

                val validator = ExerciseValidator.getValidator(setType, set.exerciseType)
                val validatedDetails = validator.validate(set.details)

                val jsonDetails = convertDataToJson(validatedDetails)

                workoutRepo.associateExerciseToSet(index + 1, set.exerciseId, setId, jsonDetails)
            }
        }
        return setId
    }

    fun createCustomWorkout(
        trainerId: UUID
    ) {
        throw NotImplementedError("Not implemented yet.")
    }

    private fun convertDataToJson(data: Map<String, Any>): String {
        val jsonMapper = jacksonObjectMapper()

        return jsonMapper.writeValueAsString(data)
    }

    private fun Transaction.createSet(userId: UUID, name: String?, notes: String?, setType: SetType): Int {
        return if (name != null) {
            require(name.isNotEmpty()) { "Name must not be empty." }
            workoutRepo.createSet(name, notes, setType)
        } else {
            val lastSetNameId = trainerRepo.getLastSetNameId(userId)
            val nextSetName = "Set #${lastSetNameId + 1}"
            workoutRepo.createSet(nextSetName, notes, setType)
        }
    }

    private fun Transaction.validateExercise(userId: UUID, userRole: Role, exerciseId: Int) {
        when (userRole) {
            Role.INDEPENDENT_TRAINER -> {
                trainerRepo.getExerciseDetails(userId, exerciseId)
                    ?: throw WorkoutError.ExerciseNotFoundError
            }
            Role.COMPANY -> {
                companyRepo.getExerciseDetails(userId, exerciseId)
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
    }
}
