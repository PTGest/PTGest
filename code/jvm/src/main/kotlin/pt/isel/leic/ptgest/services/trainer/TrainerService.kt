package pt.isel.leic.ptgest.services.trainer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Set
import pt.isel.leic.ptgest.domain.workout.model.SetDetails
import pt.isel.leic.ptgest.domain.workout.model.SetExercise
import pt.isel.leic.ptgest.domain.workout.model.Workout
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.trainee.TraineeError
import pt.isel.leic.ptgest.services.utils.ExerciseValidator
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.UUID

@Service
class TrainerService(
    private val transactionManager: TransactionManager
) {
    fun createCustomExercise(
        trainerId: UUID,
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        modality: Modality,
        ref: String?
    ): Int {
        Validators.validate(
            Validators.ValidationRequest(description, "Description must not be empty.") { (it as String).isNotEmpty() },
            Validators.ValidationRequest(ref, "Reference must be a valid YouTube URL.") { Validators.isYoutubeUrl(it as String) }
        )

        return transactionManager.run {
            val trainerRepo = it.trainerRepo
            val workoutRepo = it.workoutRepo

            val exerciseId = workoutRepo.createExercise(
                name,
                description,
                muscleGroup,
                modality,
                ref
            )

            trainerRepo.associateTrainerToExercise(trainerId, exerciseId)

            return@run exerciseId
        }
    }

    fun getExercises(
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        trainerId: UUID,
        userRole: Role
    ): Pair<List<Exercise>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            return@run it.getExercises(
                trainerId,
                skip,
                limit,
                name,
                muscleGroup,
                modality
            )
        }
    }

    fun getExerciseDetails(
        exerciseId: Int,
        userId: UUID
    ): ExerciseDetails =
        transactionManager.run {
            val companyRepo = it.companyRepo
            val trainerRepo = it.trainerRepo

            val companyId = trainerRepo.getCompanyAssignedTrainer(userId)

            return@run trainerRepo.getExerciseDetails(userId, exerciseId)
                ?: companyId?.let { companyRepo.getExerciseDetails(companyId, exerciseId) }
                ?: throw TrainerError.ExerciseNotFoundError
        }

    fun createCustomSet(
        trainerId: UUID,
        name: String?,
        notes: String?,
        setType: SetType,
        setExercises: List<SetExercise>
    ): Int {
        Validators.validate(
            Validators.ValidationRequest(notes, "Notes must not be empty.") { (it as String).isNotEmpty() }
        )

        val setId = transactionManager.runWithLevel(TransactionIsolationLevel.SERIALIZABLE) {
            it.createSet(trainerId, name, notes, setType)
        }

        transactionManager.run {
            val workoutRepo = it.workoutRepo

            setExercises.forEachIndexed { index, set ->
                val exercise = it.getExercise(trainerId, set.exerciseId)

                val validator = ExerciseValidator.getValidator(setType, exercise.modality)
                val validatedDetails = validator.validate(set.details)

                val jsonDetails = convertDataToJson(validatedDetails)

                workoutRepo.associateExerciseToSet(index + 1, set.exerciseId, setId, jsonDetails)
            }
        }
        return setId
    }

    fun getSets(
        userId: UUID,
        skip: Int?,
        limit: Int?,
        type: SetType?,
        name: String?
    ): Pair<List<Set>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(name, "Name must be a non-empty string.")
            { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val traineeRepo = it.traineeRepo
            val trainerRepo = it.trainerRepo

            val trainerId = traineeRepo.getTrainerAssigned(userId)
                ?: throw TraineeError.TraineeNotAssigned

            val sets = trainerRepo.getSets(trainerId, skip ?: 0, limit, type, name)
            val totalSets = trainerRepo.getTotalSets(trainerId, type, name)

            return@run Pair(sets, totalSets)
        }
    }

    fun getSetDetails(
        trainerId: UUID,
        setId: Int
    ): SetDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            val set = trainerRepo.getSet(trainerId, setId)
                ?: throw TrainerError.SetNotFoundError
            val exercises = trainerRepo.getSetExercises(setId)

            return@run SetDetails(set, exercises)
        }

    fun createCustomWorkout(
        trainerId: UUID,
        name: String?,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        sets: List<Int>
    ): Int {
        Validators.validate(
            Validators.ValidationRequest(description, "Description must not be empty.") { (it as String).isNotEmpty() }
        )

        val workoutId = transactionManager.runWithLevel(TransactionIsolationLevel.SERIALIZABLE) {
            it.createWorkout(trainerId, name, description, muscleGroup)
        }

        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val trainerRepo = it.trainerRepo

            sets.forEachIndexed { index, setId ->
                trainerRepo.getSet(trainerId, setId)
                    ?: throw TrainerError.SetNotFoundError

                workoutRepo.associateSetToWorkout(index + 1, setId, workoutId)
            }
        }

        return workoutId
    }

    fun getWorkouts(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?
    ): Pair<List<Workout>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(name, "Name must be a non-empty string.") { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val trainerRepo = it.trainerRepo

            val workouts = trainerRepo.getWorkouts(trainerId, skip ?: 0, limit, name, muscleGroup)
            val totalWorkouts = trainerRepo.getTotalWorkouts(trainerId, name, muscleGroup)

            return@run Pair(workouts, totalWorkouts)
        }
    }

    fun getWorkoutDetails(
        trainerId: UUID,
        workoutId: Int
    ): WorkoutDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            val workout = trainerRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TrainerError.WorkoutNotFoundError

            val sets = trainerRepo.getWorkoutSetIds(workoutId).map { setId ->
                val set = trainerRepo.getSet(setId)
                val exercises = trainerRepo.getSetExercises(setId)
                SetDetails(set, exercises)
            }

            return@run WorkoutDetails(workout, sets)
        }

    private fun Transaction.getExercises(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Pair<List<Exercise>, Int> {
        val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

        val resultList = mutableListOf<Exercise>()
        var totalExercises = 0

        getTrainerExercises(
            trainerId,
            skip,
            limit,
            name,
            muscleGroup,
            modality,
            { resultList += it },
            { totalExercises += it }
        )

        getCompanyExercises(
            companyId,
            skip,
            limit,
            name,
            muscleGroup,
            modality,
            { resultList += it },
            { totalExercises += it }
        )

        return Pair(resultList, totalExercises)
    }

    private fun Transaction.getTrainerExercises(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        addExercises: (List<Exercise>) -> Unit,
        addTotalExercises: (Int) -> Unit
    ) {
        val trainerExercises = trainerRepo.getExercises(
            trainerId,
            skip ?: 0,
            limit,
            name,
            muscleGroup,
            modality
        )
        val totalTrainerExercises = trainerRepo.getTotalExercises(
            trainerId,
            name,
            muscleGroup,
            modality
        )

        addExercises(trainerExercises)
        addTotalExercises(totalTrainerExercises)
    }

    private fun Transaction.getCompanyExercises(
        companyId: UUID?,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        addExercises: (List<Exercise>) -> Unit,
        addTotalExercises: (Int) -> Unit
    ) {
        companyId?.let {
            val companyExercises = companyRepo.getExercises(
                companyId,
                skip ?: 0,
                limit,
                name,
                muscleGroup,
                modality
            )
            val totalCompanyExercises = companyRepo.getTotalExercises(
                companyId,
                name,
                muscleGroup,
                modality
            )

            addExercises(companyExercises)
            addTotalExercises(totalCompanyExercises)
        }
    }

    private fun convertDataToJson(data: Map<String, Any>): String {
        val jsonMapper = jacksonObjectMapper()

        return jsonMapper.writeValueAsString(data)
    }

    private fun Transaction.createSet(
        trainerId: UUID,
        name: String?,
        notes: String?,
        setType: SetType
    ): Int =
        if (name != null) {
            require(name.isNotEmpty()) { "Name must not be empty." }
            workoutRepo.createSet(trainerId, name, notes, setType)
        } else {
            val lastSetNameId = trainerRepo.getLastSetNameId(trainerId)
            val nextSetName = "Set #${lastSetNameId + 1}"
            workoutRepo.createSet(trainerId, nextSetName, notes, setType)
        }

    private fun Transaction.getExercise(userId: UUID, exerciseId: Int): ExerciseDetails {
        val companyId = trainerRepo.getCompanyAssignedTrainer(userId)

        return trainerRepo.getExerciseDetails(userId, exerciseId)
            ?: companyId?.let { companyRepo.getExerciseDetails(companyId, exerciseId) }
            ?: throw TrainerError.ExerciseNotFoundError
    }

    private fun Transaction.createWorkout(
        trainerId: UUID,
        name: String?,
        description: String?,
        muscleGroup: List<MuscleGroup>
    ): Int =
        if (name != null) {
            require(name.isNotEmpty()) { "Name must not be empty." }
            workoutRepo.createWorkout(trainerId, name, description, muscleGroup)
        } else {
            val lastWorkoutNameId = trainerRepo.getLastWorkoutNameId(trainerId)
            val nextWorkoutName = "Workout #${lastWorkoutNameId + 1}"
            workoutRepo.createWorkout(trainerId, nextWorkoutName, description, muscleGroup)
        }
}
