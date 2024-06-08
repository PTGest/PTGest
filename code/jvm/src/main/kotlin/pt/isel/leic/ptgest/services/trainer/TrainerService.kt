package pt.isel.leic.ptgest.services.trainer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.traineeData.MeasurementTechnique
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.traineeData.SkinFold
import pt.isel.leic.ptgest.domain.traineeData.model.BodyCircumferences
import pt.isel.leic.ptgest.domain.traineeData.model.BodyComposition
import pt.isel.leic.ptgest.domain.traineeData.model.BodyData
import pt.isel.leic.ptgest.domain.report.model.Report
import pt.isel.leic.ptgest.domain.report.model.ReportDetails
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
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.*

@Service
class TrainerService(
    private val transactionManager: TransactionManager
) {
    //  Trainee data related Services
    fun addTraineeData(
        trainerId: UUID,
        traineeId: UUID,
        traineeGender: Gender,
        weight: Double,
        height: Double,
        bodyCircumferences: BodyCircumferences,
        bodyFatPercentage: Double?,
        skinFold: Map<SkinFold, Double>?
    ): Int {
        val requestDate = Date()

        require(weight > 0) { "Weight must be a positive number." }
        require(height > 0) { "Height must be a positive number." }

        return transactionManager.run {
            val traineeRepo = it.traineeRepo
            val userRepo = it.userRepo
            val trainerRepo = it.trainerRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val traineeBirthdate = userRepo.getTraineeDetails(traineeId)?.birthdate
                ?: throw TraineeError.TraineeNotFound

            val bodyData = getBodyData(
                traineeGender,
                calculateAge(traineeBirthdate),
                weight,
                height,
                bodyCircumferences,
                bodyFatPercentage,
                skinFold
            )

            return@run trainerRepo.addTraineeData(
                traineeId,
                requestDate,
                convertDataToJson(bodyData)
            )
        }
    }

    //  Report related Services
    fun createReport(
        trainerId: UUID,
        traineeId: UUID,
        report: String,
        visibility: Boolean
    ): Int {
        val requestDate = Date()
        return transactionManager.run {
            val traineeRepo = it.traineeRepo
            val trainerRepo = it.trainerRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val reportId = trainerRepo.createReport(traineeId, requestDate, report, visibility)

            trainerRepo.associateTrainerToReport(trainerId, reportId)
            return@run reportId
        }
    }

    fun getReports(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        traineeId: UUID?,
        traineeName: String?
    ): Pair<List<Report>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(
                traineeName,
                "Name must be a non-empty string."
            ) { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val trainerRepo = it.trainerRepo
            val traineeRepo = it.traineeRepo

            val searchTraineeId = traineeId ?: traineeName?.let { trainerRepo.getTraineeIdByName(it) }

            if (searchTraineeId != null) {
                val traineeTrainerId = traineeRepo.getTrainerAssigned(searchTraineeId)
                    ?: throw TraineeError.TraineeNotAssigned

                if (traineeTrainerId != trainerId) {
                    throw TrainerError.TraineeNotAssignedToTrainerError
                }
            }

            val reports = trainerRepo.getReports(trainerId, skip ?: 0, limit, searchTraineeId)
            val totalReports = trainerRepo.getTotalReports(trainerId, searchTraineeId)

            return@run Pair(reports, totalReports)
        }
    }

    fun getReportDetails(
        trainerId: UUID,
        reportId: Int
    ): ReportDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            return@run trainerRepo.getReportDetails(trainerId, reportId)
                ?: throw TrainerError.ResourceNotFoundError
        }

    fun editReport(
        trainerId: UUID,
        reportId: Int,
        report: String,
        visibility: Boolean
    ) {
        val requestDate = Date()

        transactionManager.run {
            val trainerRepo = it.trainerRepo

            trainerRepo.getReportDetails(trainerId, reportId)
                ?: throw TrainerError.ResourceNotFoundError

            trainerRepo.editReport(reportId, requestDate, report, visibility)
        }
    }

    fun deleteReport(trainerId: UUID, reportId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
//          todo: check error
            trainerRepo.getReportDetails(trainerId, reportId)
                ?: throw TrainerError.ResourceNotFoundError

            trainerRepo.deleteReport(reportId)
        }
    }

    //  Exercise related Services
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
            Validators.ValidationRequest(
                ref,
                "Reference must be a valid YouTube URL."
            ) { Validators.isYoutubeUrl(it as String) }
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
        trainerId: UUID,
        userRole: Role,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        favorite: Boolean
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
                modality,
                favorite
            )
        }
    }

    fun getExerciseDetails(
        trainerId: UUID,
        exerciseId: Int
    ): ExerciseDetails =
        transactionManager.run {
            val companyRepo = it.companyRepo
            val trainerRepo = it.trainerRepo

            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            return@run trainerRepo.getExerciseDetails(trainerId, exerciseId)
                ?: companyId?.let { companyRepo.getExerciseDetails(companyId, exerciseId) }
                ?: throw TrainerError.ResourceNotFoundError
        }

    fun deleteExercise(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val workoutRepo = it.workoutRepo

            trainerRepo.getExerciseDetails(trainerId, exerciseId)
                ?: throw TrainerError.ResourceNotFoundError

            workoutRepo.deleteExercise(exerciseId)
        }
    }

    fun favoriteExercise(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val companyRepo = it.companyRepo

            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            trainerRepo.getExerciseDetails(trainerId, exerciseId)
                ?: companyId?.let { companyRepo.getExerciseDetails(companyId, exerciseId) }
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteExercises = trainerRepo.getFavoriteExercisesByTrainerId(trainerId)

            if (exerciseId in favoriteExercises) {
                throw TrainerError.ResourceAlreadyFavoriteError
            }

            require(exerciseId !in favoriteExercises) { "Exercise is already favorited." }

            trainerRepo.favoriteExercise(trainerId, exerciseId)
        }
    }

    fun unfavoriteExercise(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val companyRepo = it.companyRepo

            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            trainerRepo.getExerciseDetails(trainerId, exerciseId)
                ?: companyId?.let { companyRepo.getExerciseDetails(companyId, exerciseId) }
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteExercises = trainerRepo.getFavoriteExercisesByTrainerId(trainerId)

            if (exerciseId !in favoriteExercises) {
                throw TrainerError.ResourceNotFavoriteError
            }

            trainerRepo.unfavoriteExercise(trainerId, exerciseId)
        }
    }

    //  Set related Services
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

        if (setType != SetType.SUPERSET) {
            require(setExercises.size == 1) { "Only one exercise is allowed for this set type." }
        }

        return transactionManager.runWithLevel(TransactionIsolationLevel.SERIALIZABLE) {
            val workoutRepo = it.workoutRepo
            val setId = it.createSet(trainerId, name, notes, setType)

            setExercises.forEachIndexed { index, set ->
                it.getExercise(trainerId, set.exerciseId)

                val validatedDetails = Validators.validateSetDetails(set.details)
                val jsonDetails = convertDataToJson(validatedDetails)

                workoutRepo.associateExerciseToSet(index + 1, set.exerciseId, setId, jsonDetails)
            }

            setId
        }
    }

    fun getSets(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        type: SetType?,
        name: String?,
        favorite: Boolean
    ): Pair<List<Set>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(name, "Name must be a non-empty string.") { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val trainerRepo = it.trainerRepo

            return@run if (favorite) {
                val sets = trainerRepo.getFavoriteSets(trainerId, skip ?: 0, limit, type, name)
                val totalSets = trainerRepo.getTotalFavoriteSets(trainerId, type, name)

                Pair(sets, totalSets)
            } else {
                val sets = trainerRepo.getSets(trainerId, skip ?: 0, limit, type, name)
                val totalSets = trainerRepo.getTotalSets(trainerId, type, name)

                Pair(sets, totalSets)
            }
        }
    }

    fun getSetDetails(
        trainerId: UUID,
        setId: Int
    ): SetDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            val set = trainerRepo.getSet(trainerId, setId)
                ?: throw TrainerError.ResourceNotFoundError
            val exercises = trainerRepo.getSetExercises(setId)

            return@run SetDetails(set, exercises)
        }

    fun deleteSet(trainerId: UUID, setId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val workoutRepo = it.workoutRepo

            trainerRepo.getSet(trainerId, setId)
                ?: throw TrainerError.ResourceNotFoundError

            workoutRepo.deleteSet(setId)
        }
    }

    fun favoriteSet(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            trainerRepo.getSet(trainerId, exerciseId)
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteSets = trainerRepo.getFavoriteSetsByTrainerId(trainerId)

            if (exerciseId in favoriteSets) {
                throw TrainerError.ResourceAlreadyFavoriteError
            }

            trainerRepo.favoriteSet(trainerId, exerciseId)
        }
    }

    fun unfavoriteSet(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            trainerRepo.getSet(trainerId, exerciseId)
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteSets = trainerRepo.getFavoriteSetsByTrainerId(trainerId)

            if (exerciseId !in favoriteSets) {
                throw TrainerError.ResourceNotFavoriteError
            }

            trainerRepo.unfavoriteSet(trainerId, exerciseId)
        }
    }

    //  Workout related Services
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
                    ?: throw TrainerError.ResourceNotFoundError

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
        muscleGroup: MuscleGroup?,
        favorite: Boolean
    ): Pair<List<Workout>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(name, "Name must be a non-empty string.") { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val trainerRepo = it.trainerRepo

            return@run if (favorite) {
                val workouts = trainerRepo.getFavoriteWorkouts(trainerId, skip ?: 0, limit, name, muscleGroup)
                val totalWorkouts = trainerRepo.getTotalFavoriteWorkouts(trainerId, name, muscleGroup)

                return@run Pair(workouts, totalWorkouts)
            } else {
                val workouts = trainerRepo.getWorkouts(trainerId, skip ?: 0, limit, name, muscleGroup)
                val totalWorkouts = trainerRepo.getTotalWorkouts(trainerId, name, muscleGroup)

                Pair(workouts, totalWorkouts)
            }
        }
    }

    fun getWorkoutDetails(
        trainerId: UUID,
        workoutId: Int
    ): WorkoutDetails =
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            val workout = trainerRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TrainerError.ResourceNotFoundError

            val sets = trainerRepo.getWorkoutSetIds(workoutId).mapNotNull { setId ->
                val set = trainerRepo.getSet(trainerId, setId) ?: return@mapNotNull null
                val exercises = trainerRepo.getSetExercises(setId)
                SetDetails(set, exercises)
            }

            return@run WorkoutDetails(workout, sets)
        }

    fun deleteWorkout(trainerId: UUID, workoutId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val workoutRepo = it.workoutRepo

            trainerRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TrainerError.ResourceNotFoundError

            workoutRepo.deleteWorkout(workoutId)
        }
    }

    fun favoriteWorkout(trainerId: UUID, workoutId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            trainerRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteWorkouts = trainerRepo.getFavoriteWorkoutsByTrainerId(trainerId)

            if (workoutId in favoriteWorkouts) {
                throw TrainerError.ResourceAlreadyFavoriteError
            }

            trainerRepo.favoriteWorkout(trainerId, workoutId)
        }
    }

    fun unfavoriteWorkout(trainerId: UUID, workoutId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo

            trainerRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteWorkouts = trainerRepo.getFavoriteWorkoutsByTrainerId(trainerId)

            if (workoutId !in favoriteWorkouts) {
                throw TrainerError.ResourceNotFavoriteError
            }

            trainerRepo.unfavoriteWorkout(trainerId, workoutId)
        }
    }

    //  Session related Services
    fun createSession(
        trainerId: UUID,
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        type: SessionType,
        notes: String?
    ): Int {
        Validators.validate(
            Validators.ValidationRequest(notes, "Notes must not be empty.") { (it as String).isNotEmpty() },
            Validators.ValidationRequest(
                endDate,
                "End date must be after begin date."
            ) { (it as Date).after(beginDate) }
        )

        return transactionManager.run {
            val traineeRepo = it.traineeRepo
            val trainerRepo = it.trainerRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            return@run trainerRepo.createSession(
                trainerId,
                traineeId,
                workoutId,
                beginDate,
                endDate,
                type,
                notes
            )
        }
    }

    private fun convertDataToJson(data: Any): String {
        val jsonMapper = jacksonObjectMapper()

        return jsonMapper.writeValueAsString(data)
    }

    private fun getBodyData(
        traineeGender: Gender,
        traineeAge: Int,
        weight: Double,
        height: Double,
        bodyCircumferences: BodyCircumferences,
        bodyFatPercentage: Double?,
        skinFold: Map<SkinFold, Double>?
    ): BodyData =
        when {
            bodyFatPercentage != null -> {
                require(bodyFatPercentage > 0) { "Body fat percentage must be a positive number." }
                val bodyComposition = BodyComposition(weight, height, bodyFatPercentage)
                BodyData(weight, height, bodyCircumferences, bodyComposition)
            }
            skinFold != null -> {
                val bodyFatPercentageResult = MeasurementTechnique.getTechnique(skinFold.size)
                    .bodyFatCalculator(
                        traineeGender,
                        skinFold,
                        traineeAge
                    )

                val bodyComposition = BodyComposition(weight, height, bodyFatPercentageResult)

                BodyData(weight, height, bodyCircumferences, bodyComposition, skinFold)
            }
            else -> {
                val bodyComposition = BodyComposition(weight, height)
                BodyData(weight, height, bodyCircumferences, bodyComposition)
            }
        }

    private fun calculateAge(birthdate: Date): Int {
        val birthdateCalendar = Calendar.getInstance().apply { time = birthdate }
        val currentCalendar = Calendar.getInstance()

        var age = currentCalendar.get(Calendar.YEAR) - birthdateCalendar.get(Calendar.YEAR)
        if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthdateCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    private fun Transaction.getExercises(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        favorite: Boolean
    ): Pair<List<Exercise>, Int> {
        val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

        val resultList = mutableListOf<Exercise>()
        var totalExercises = 0

        if (favorite) {
            return getFavoriteExercises(trainerId, skip, limit, name, muscleGroup, modality)
        }

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

    private fun Transaction.getFavoriteExercises(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Pair<List<Exercise>, Int> {
        val favoriteExercises = trainerRepo.getFavoriteExercises(
            trainerId,
            skip ?: 0,
            limit,
            name,
            muscleGroup,
            modality
        )
        val totalFavoriteExercises = trainerRepo.getTotalFavoriteExercises(
            trainerId,
            name,
            muscleGroup,
            modality
        )

        return Pair(favoriteExercises, totalFavoriteExercises)
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
            ?: throw TrainerError.ResourceNotFoundError
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
