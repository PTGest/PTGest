package pt.isel.leic.ptgest.services.trainer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.SetDetails
import pt.isel.leic.ptgest.domain.common.SetType
import pt.isel.leic.ptgest.repository.transaction.Transaction
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
        category: ExerciseType,
        ref: String?
    ) {
        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val trainerRepo = it.trainerRepo

            val exerciseId = workoutRepo.createExercise(name, description, category, ref)
            trainerRepo.associateTrainerToExercise(exerciseId, trainerId)
        }
    }

//  TODO: check if the user can use the exercise
//  TODO: check if could have concurrency problems
    fun createCustomSet(
        trainerId: UUID,
        name: String?,
        notes: String?,
        details: SetDetails
    ) {
        val jsonDetails = convertDetailsToJson(details)

        if (notes != null) {
            require(notes.isNotEmpty()) { "Notes must not be empty." }
        }

        transactionManager.run {
            val setName = if (name != null) {
                require(name.isNotEmpty()) { "Name must not be empty." }
                name
            } else {
                val lastNameId = it.trainerRepo.getLastSetNameId(trainerId)
                "Set #${lastNameId + 1}"
            }

            if (details is SetDetails.SuperSet) {
                it.createSuperSet(details.exercises, trainerId, setName, notes, jsonDetails)
            } else {
                val setType = SetDetails.setTypeMap[details::class]
                    ?: throw TrainerError.InvalidSetTypeError

                it.createSet(trainerId, details.exercise, setName, notes, setType, jsonDetails)
            }
        }
    }

    private fun convertDetailsToJson(details: SetDetails): String {
        val jsonMapper = jacksonObjectMapper()

        return if (details is SetDetails.SuperSet) {
            jsonMapper.writeValueAsString(details.toMapDetailsList())
        } else {
            jsonMapper.writeValueAsString(details.toMapDetails())
        }
    }

    private fun Transaction.createSuperSet(
        exercises: List<SetDetails.SuperSetExercise>,
        trainerId: UUID,
        name: String,
        notes: String?,
        details: String
    ) {
        exercises.forEach { set ->
            workoutRepo.getExerciseDetails(set.exercise)
                ?: throw TrainerError.ExerciseNotFoundError
        }

        val setId = workoutRepo.createSet(name, notes, SetType.SUPERSET, details)
        trainerRepo.associateTrainerToSet(setId, trainerId)

        exercises.forEach { set ->
            workoutRepo.associateExerciseToSet(set.exercise, setId)
        }
    }

    private fun Transaction.createSet(
        trainerId: UUID,
        exerciseId: Int,
        name: String,
        notes: String?,
        type: SetType,
        details: String
    ) {
        workoutRepo.getExerciseDetails(exerciseId)
            ?: throw TrainerError.ExerciseNotFoundError

        val setId = workoutRepo.createSet(name, notes, type, details)
        trainerRepo.associateTrainerToSet(setId, trainerId)
        workoutRepo.associateExerciseToSet(exerciseId, setId)
    }
}
