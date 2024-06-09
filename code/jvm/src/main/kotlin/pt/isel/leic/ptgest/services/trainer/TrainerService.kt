package pt.isel.leic.ptgest.services.trainer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.exercise.model.Exercise
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.report.model.Report
import pt.isel.leic.ptgest.domain.report.model.ReportDetails
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.set.ExerciseDetailsType
import pt.isel.leic.ptgest.domain.set.model.Set
import pt.isel.leic.ptgest.domain.set.model.SetDetails
import pt.isel.leic.ptgest.domain.set.model.SetExercise
import pt.isel.leic.ptgest.domain.trainee.model.Trainee
import pt.isel.leic.ptgest.domain.traineeData.MeasurementTechnique
import pt.isel.leic.ptgest.domain.traineeData.SkinFold
import pt.isel.leic.ptgest.domain.traineeData.model.BodyCircumferences
import pt.isel.leic.ptgest.domain.traineeData.model.BodyComposition
import pt.isel.leic.ptgest.domain.traineeData.model.BodyData
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeData
import pt.isel.leic.ptgest.domain.traineeData.model.TraineeDataDetails
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.Workout
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.repository.transaction.Transaction
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.trainee.TraineeError
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.Calendar
import java.util.Date
import java.util.UUID

@Service
class TrainerService(
    private val transactionManager: TransactionManager
) {

    fun getTrainerTrainees(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        gender: Gender?,
        name: String?
    ): Pair<List<Trainee>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val trainerRepo = it.trainerRepo

            val trainees = trainerRepo.getTrainees(trainerId, skip ?: 0, limit, gender, name)
            val totalTrainees = trainerRepo.getTotalTrainees(trainerId, gender, name)

            return@run Pair(trainees, totalTrainees)
        }
    }

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
            val traineeDataRepo = it.traineeDataRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val traineeBirthdate = traineeRepo.getTraineeDetails(traineeId)?.birthdate
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

            return@run traineeDataRepo.addTraineeData(
                traineeId,
                requestDate,
                convertDataToJson(bodyData)
            )
        }
    }

    fun getTraineeDataHistory(
        trainerId: UUID,
        traineeId: UUID,
        skip: Int?,
        limit: Int?,
        order: Order?,
        date: Date?
    ): Pair<List<TraineeData>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(date, "Date must be before the current date.") { (it as Date).before(Date()) }
        )

        return transactionManager.run {
            val traineeRepo = it.traineeRepo
            val traineeDataRepo = it.traineeDataRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val traineeData = traineeDataRepo.getTraineeData(
                traineeId,
                skip ?: 0,
                limit,
                order ?: Order.DESC,
                date
            )

            val totalTraineeData = traineeDataRepo.getTotalTraineeData(traineeId, date)

            return@run Pair(traineeData, totalTraineeData)
        }
    }

    fun getTraineeDataDetails(
        trainerId: UUID,
        traineeId: UUID,
        dataId: Int
    ): TraineeDataDetails =
        transactionManager.run {
            val traineeRepo = it.traineeRepo
            val traineeDataRepo = it.traineeDataRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            return@run traineeDataRepo.getTraineeBodyDataDetails(traineeId, dataId)
                ?: throw TrainerError.ResourceNotFoundError
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
            val reportRepo = it.reportRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val reportId = reportRepo.createReport(traineeId, requestDate, report, visibility)

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
            val reportRepo = it.reportRepo

            val searchTraineeId = traineeId ?: traineeName?.let { trainerRepo.getTraineeIdByName(it) }

            if (searchTraineeId != null) {
                val traineeTrainerId = traineeRepo.getTrainerAssigned(searchTraineeId)
                    ?: throw TraineeError.TraineeNotAssigned

                if (traineeTrainerId != trainerId) {
                    throw TrainerError.TraineeNotAssignedToTrainerError
                }
            }

            val reports = reportRepo.getReports(trainerId, skip ?: 0, limit, searchTraineeId)
            val totalReports = reportRepo.getTotalReports(trainerId, searchTraineeId)

            return@run Pair(reports, totalReports)
        }
    }

    fun getReportDetails(
        trainerId: UUID,
        reportId: Int
    ): ReportDetails =
        transactionManager.run {
            val reportRepo = it.reportRepo

            return@run reportRepo.getReportDetails(trainerId, reportId)
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
            val reportRepo = it.reportRepo

            reportRepo.getReportDetails(trainerId, reportId)
                ?: throw TrainerError.ResourceNotFoundError

            reportRepo.editReport(reportId, requestDate, report, visibility)
        }
    }

    fun deleteReport(trainerId: UUID, reportId: Int) {
        transactionManager.run {
            val reportRepo = it.reportRepo
//          todo: check error
            reportRepo.getReportDetails(trainerId, reportId)
                ?: throw TrainerError.ResourceNotFoundError

            reportRepo.deleteReport(reportId)
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
            val exerciseRepo = it.exerciseRepo

            val exerciseId = exerciseRepo.createExercise(
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
            val trainerRepo = it.trainerRepo
            val exerciseRepo = it.exerciseRepo

            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            return@run exerciseRepo.getTrainerExerciseDetails(trainerId, exerciseId)
                ?: companyId?.let { exerciseRepo.getCompanyExerciseDetails(companyId, exerciseId) }
                ?: throw TrainerError.ResourceNotFoundError
        }

    fun favoriteExercise(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val exerciseRepo = it.exerciseRepo

            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            exerciseRepo.getTrainerExerciseDetails(trainerId, exerciseId)
                ?: companyId?.let { exerciseRepo.getCompanyExerciseDetails(companyId, exerciseId) }
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteExercises = exerciseRepo.getFavoriteExercises(trainerId)

            if (exerciseId in favoriteExercises) {
                throw TrainerError.ResourceAlreadyFavoriteError
            }
//          todo: verify error
            require(exerciseId !in favoriteExercises) { "Exercise is already favorited." }

            exerciseRepo.favoriteExercise(trainerId, exerciseId)
        }
    }

    fun unfavoriteExercise(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val exerciseRepo = it.exerciseRepo

            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            exerciseRepo.getTrainerExerciseDetails(trainerId, exerciseId)
                ?: companyId?.let { exerciseRepo.getCompanyExerciseDetails(companyId, exerciseId) }
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteExercises = exerciseRepo.getFavoriteExercises(trainerId)

            if (exerciseId !in favoriteExercises) {
                throw TrainerError.ResourceNotFavoriteError
            }

            exerciseRepo.unfavoriteExercise(trainerId, exerciseId)
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
            val setRepo = it.setRepo

            val setId = it.createSet(trainerId, name, notes, setType)

            setExercises.forEachIndexed { index, set ->
                it.getExercise(trainerId, set.exerciseId)

                val validatedDetails = ExerciseDetailsType.validateSetDetails(set.details)
                val jsonDetails = convertDataToJson(validatedDetails)

                setRepo.associateExerciseToSet(index + 1, set.exerciseId, setId, jsonDetails)
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
            val setRepo = it.setRepo

            return@run if (favorite) {
                val sets = setRepo.getFavoriteSets(trainerId, skip ?: 0, limit, type, name)
                val totalSets = setRepo.getTotalFavoriteSets(trainerId, type, name)

                Pair(sets, totalSets)
            } else {
                val sets = setRepo.getSets(trainerId, skip ?: 0, limit, type, name)
                val totalSets = setRepo.getTotalSets(trainerId, type, name)

                Pair(sets, totalSets)
            }
        }
    }

    fun getSetDetails(
        trainerId: UUID,
        setId: Int
    ): SetDetails =
        transactionManager.run {
            val setRepo = it.setRepo

            val set = setRepo.getSet(trainerId, setId)
                ?: throw TrainerError.ResourceNotFoundError
            val exercises = setRepo.getSetExercises(setId)

            return@run SetDetails(set, exercises)
        }

    fun favoriteSet(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val setRepo = it.setRepo

            setRepo.getSet(trainerId, exerciseId)
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteSets = setRepo.getFavoriteSetsByTrainerId(trainerId)

            if (exerciseId in favoriteSets) {
                throw TrainerError.ResourceAlreadyFavoriteError
            }

            setRepo.favoriteSet(trainerId, exerciseId)
        }
    }

    fun unfavoriteSet(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val setRepo = it.setRepo

            setRepo.getSet(trainerId, exerciseId)
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteSets = setRepo.getFavoriteSetsByTrainerId(trainerId)

            if (exerciseId !in favoriteSets) {
                throw TrainerError.ResourceNotFavoriteError
            }

            setRepo.unfavoriteSet(trainerId, exerciseId)
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
            val setRepo = it.setRepo

            sets.forEachIndexed { index, setId ->
                setRepo.getSet(trainerId, setId)
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
            val workoutRepo = it.workoutRepo

            return@run if (favorite) {
                val workouts = workoutRepo.getFavoriteWorkouts(trainerId, skip ?: 0, limit, name, muscleGroup)
                val totalWorkouts = workoutRepo.getTotalFavoriteWorkouts(trainerId, name, muscleGroup)

                return@run Pair(workouts, totalWorkouts)
            } else {
                val workouts = workoutRepo.getWorkouts(trainerId, skip ?: 0, limit, name, muscleGroup)
                val totalWorkouts = workoutRepo.getTotalWorkouts(trainerId, name, muscleGroup)

                Pair(workouts, totalWorkouts)
            }
        }
    }

    fun getWorkoutDetails(
        trainerId: UUID,
        workoutId: Int
    ): WorkoutDetails =
        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val setRepo = it.setRepo

            val workout = workoutRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TrainerError.ResourceNotFoundError

            val sets = workoutRepo.getWorkoutSetIds(workoutId).mapNotNull { setId ->
                val set = setRepo.getSet(trainerId, setId) ?: return@mapNotNull null
                val exercises = setRepo.getSetExercises(setId)
                SetDetails(set, exercises)
            }

            return@run WorkoutDetails(workout, sets)
        }

    fun favoriteWorkout(trainerId: UUID, workoutId: Int) {
        transactionManager.run {
            val workoutRepo = it.workoutRepo

            workoutRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteWorkouts = workoutRepo.getFavoriteWorkoutsByTrainerId(trainerId)

            if (workoutId in favoriteWorkouts) {
                throw TrainerError.ResourceAlreadyFavoriteError
            }

            workoutRepo.favoriteWorkout(trainerId, workoutId)
        }
    }

    fun unfavoriteWorkout(trainerId: UUID, workoutId: Int) {
        transactionManager.run {
            val workoutRepo = it.workoutRepo

            workoutRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw TrainerError.ResourceNotFoundError

            val favoriteWorkouts = workoutRepo.getFavoriteWorkoutsByTrainerId(trainerId)

            if (workoutId !in favoriteWorkouts) {
                throw TrainerError.ResourceNotFavoriteError
            }

            workoutRepo.unfavoriteWorkout(trainerId, workoutId)
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
            val sessionRepo = it.sessionRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssigned

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            return@run sessionRepo.createSession(
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
        val favoriteExercises = exerciseRepo.getFavoriteExercises(
            trainerId,
            skip ?: 0,
            limit,
            name,
            muscleGroup,
            modality
        )
        val totalFavoriteExercises = exerciseRepo.getTotalFavoriteExercises(
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
        val trainerExercises = exerciseRepo.getTrainerExercises(
            trainerId,
            skip ?: 0,
            limit,
            name,
            muscleGroup,
            modality
        )
        val totalTrainerExercises = exerciseRepo.getTotalTrainerExercises(
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
            val companyExercises = exerciseRepo.getCompanyExercises(
                companyId,
                skip ?: 0,
                limit,
                name,
                muscleGroup,
                modality
            )
            val totalCompanyExercises = exerciseRepo.getTotalCompanyExercises(
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
            setRepo.createSet(trainerId, name, notes, setType)
        } else {
            val lastSetNameId = setRepo.getLastSetNameId(trainerId)
            val nextSetName = "Set #${lastSetNameId + 1}"
            setRepo.createSet(trainerId, nextSetName, notes, setType)
        }

    private fun Transaction.getExercise(userId: UUID, exerciseId: Int): ExerciseDetails {
        val companyId = trainerRepo.getCompanyAssignedTrainer(userId)

        return exerciseRepo.getTrainerExerciseDetails(userId, exerciseId)
            ?: companyId?.let { exerciseRepo.getCompanyExerciseDetails(companyId, exerciseId) }
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
            val lastWorkoutNameId = workoutRepo.getLastWorkoutNameId(trainerId)
            val nextWorkoutName = "Workout #${lastWorkoutNameId + 1}"
            workoutRepo.createWorkout(trainerId, nextWorkoutName, description, muscleGroup)
        }
}
