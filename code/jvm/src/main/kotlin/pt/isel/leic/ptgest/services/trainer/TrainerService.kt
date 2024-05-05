package pt.isel.leic.ptgest.services.trainer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.MuscleGroup
import pt.isel.leic.ptgest.domain.common.SetDetails
import pt.isel.leic.ptgest.domain.common.SetType
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.UUID

@Service
class TrainerService(
    private val transactionManager: TransactionManager
) {

    fun createCustomExercise(
        trainerId: UUID,
        name: String,
        description: String?,
        muscleGroup: MuscleGroup,
        exerciseType: ExerciseType,
        ref: String?
    ): Int =
        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val trainerRepo = it.trainerRepo

            val exerciseId = workoutRepo.createExercise(name, description, muscleGroup, exerciseType, ref)
            trainerRepo.associateTrainerToExercise(exerciseId, trainerId)

            return@run exerciseId
        }

    fun createCustomSet(
        trainerId: UUID,
        name: String?,
        notes: String?,
        setType: SetType,
        sets: List<SetDetails>
    ) {
        if (notes != null) {
            require(notes.isNotEmpty()) { "Notes must not be empty." }
        }

        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val trainerRepo = it.trainerRepo

            val setId = if (name != null) {
                require(name.isNotEmpty()) { "Name must not be empty." }
                workoutRepo.createSet(name, notes, setType)
            } else {
                val lastSetNameId = trainerRepo.getLastSetNameId(trainerId)
                val nextSetName = "Set #${lastSetNameId + 1}"
                workoutRepo.createSet(nextSetName, notes, setType)
            }

            sets.forEachIndexed { index, set ->
                workoutRepo.getExerciseDetails(set.exerciseId)
                    ?: throw TrainerError.ExerciseNotFoundError

                val jsonDetails = convertDetailsToJson(set, setType)

                workoutRepo.associateExerciseToSet(index + 1, set.exerciseId, setId, jsonDetails)
            }

            trainerRepo.associateTrainerToSet(setId, trainerId)
        }
    }

    fun createCustomWorkout(
        trainerId: UUID
    ) {
        throw NotImplementedError("Not implemented yet.")
    }

    private fun convertDetailsToJson(set: SetDetails, setType: SetType): String {
        val jsonMapper = jacksonObjectMapper()

        val validator = ExerciseValidator.getValidator(setType, set.exerciseType)
        val validatedDetails = validator.validate(set.details)

        return jsonMapper.writeValueAsString(validatedDetails)
    }
}
