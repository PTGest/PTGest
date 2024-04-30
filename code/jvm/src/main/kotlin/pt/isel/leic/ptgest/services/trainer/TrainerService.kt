package pt.isel.leic.ptgest.services.trainer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.ExerciseType
import pt.isel.leic.ptgest.domain.common.SetDetails
import pt.isel.leic.ptgest.domain.common.SetDetails.SuperSetExercise
import pt.isel.leic.ptgest.domain.common.SetType
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import java.util.*

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
        if (name != null) {
            require(name.isNotEmpty()) { "Name must not be empty." }

            when (details) {
                is SetDetails.DropSet -> {
                    transactionManager.run {
                        it.createSet(trainerId, details.exercise, name, notes, SetType.DROPSET, jsonDetails)
                    }
                }
                is SetDetails.SuperSet -> {
                    transactionManager.run {
                        it.createSuperSet(details.exercises, trainerId, name, notes, jsonDetails)
                    }
                }
                is SetDetails.Running -> {
                    transactionManager.run {
                        it.createSet(trainerId, details.exercise, name, notes, SetType.RUNNING, jsonDetails)
                    }
                }
                is SetDetails.BodyWeight -> {
                    transactionManager.run {
                        it.createSet(trainerId, details.exercise, name, notes, SetType.BODYWEIGHT, jsonDetails)
                    }
                }
                is SetDetails.WeightedLift -> {
                    transactionManager.run {
                        it.createSet(trainerId, details.exercise, name, notes, SetType.WEIGHTEDLIFT, jsonDetails)
                    }
                }
            }
        }
    }

    private fun convertDetailsToJson(details: SetDetails): String {
        val jsonMapper = jacksonObjectMapper()

        when (details) {
            is SetDetails.DropSet -> {
                return jsonMapper.writeValueAsString(details.toMapDetails())
            }
            is SetDetails.SuperSet -> {
                return jsonMapper.writeValueAsString(details.toMapDetails())
            }
            is SetDetails.Running -> {
                return jsonMapper.writeValueAsString(details.toMapDetails())
            }
            is SetDetails.BodyWeight -> {
                return jsonMapper.writeValueAsString(details.toMapDetails())
            }
            is SetDetails.WeightedLift -> {
                return jsonMapper.writeValueAsString(details.toMapDetails())
            }
        }
    }

    private fun Transaction.createSuperSet(
        exercises: List<SuperSetExercise>,
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
