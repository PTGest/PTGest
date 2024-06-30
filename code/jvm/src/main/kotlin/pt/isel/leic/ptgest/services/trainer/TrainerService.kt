package pt.isel.leic.ptgest.services.trainer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.exercise.model.Exercise
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.exercise.model.TrainerExercise
import pt.isel.leic.ptgest.domain.report.model.Report
import pt.isel.leic.ptgest.domain.report.model.ReportDetails
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.session.model.Session
import pt.isel.leic.ptgest.domain.session.model.SessionFeedback
import pt.isel.leic.ptgest.domain.session.model.SetSessionFeedback
import pt.isel.leic.ptgest.domain.session.model.TrainerSession
import pt.isel.leic.ptgest.domain.session.model.TrainerSessionDetails
import pt.isel.leic.ptgest.domain.set.ExerciseDetailsType
import pt.isel.leic.ptgest.domain.set.model.SetDetails
import pt.isel.leic.ptgest.domain.set.model.SetExercise
import pt.isel.leic.ptgest.domain.set.model.TrainerSet
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
import pt.isel.leic.ptgest.domain.workout.model.TrainerWorkout
import pt.isel.leic.ptgest.domain.workout.model.WorkoutDetails
import pt.isel.leic.ptgest.domain.workout.model.WorkoutSetDetails
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
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val traineeBirthdate = traineeRepo.getTraineeDetails(traineeId)?.birthdate
                ?: throw TraineeError.TraineeNotFoundError

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
                ?: throw TraineeError.TraineeNotAssignedError

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
                ?: throw TraineeError.TraineeNotAssignedError

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
                ?: throw TraineeError.TraineeNotAssignedError

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
                    ?: throw TraineeError.TraineeNotAssignedError

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
    ): Pair<List<TrainerExercise>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val exerciseRepo = it.exerciseRepo

            val (exercises, total) = it.getExercises(
                trainerId,
                skip,
                limit,
                name,
                muscleGroup,
                modality,
                favorite
            )

            val trainerExercises = exercises.map { exercise ->
                val isFavorite = exerciseRepo.isFavoriteExercise(trainerId, exercise.id)
                TrainerExercise(exercise, isFavorite)
            }

            return@run Pair(trainerExercises, total)
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

        val jsonDetails = setExercises.map {
            val validatedDetails = ExerciseDetailsType.validateSetDetails(it.details)
            val jsonDetails = convertDataToJson(validatedDetails)
            Pair(it.exerciseId, jsonDetails)
        }

        return transactionManager.runWithLevel(TransactionIsolationLevel.SERIALIZABLE) {
            val setRepo = it.setRepo
            val trainerRepo = it.trainerRepo

            val setId = it.createSet(trainerId, name, notes, setType)

            jsonDetails.forEachIndexed { index, pair ->
                val (exerciseId, details) = pair

                it.getExercise(trainerId, exerciseId)
                setRepo.associateExerciseToSet(index + 1, exerciseId, setId, details)
            }

            trainerRepo.associateTrainerToSet(trainerId, setId)

            return@runWithLevel setId
        }
    }

    fun searchSet(
        setType: SetType,
        setExercises: List<SetExercise>
    ): Int? {
        if (setType != SetType.SUPERSET) {
            require(setExercises.size == 1) { "Only one exercise is allowed for this set type." }
        }

        val jsonDetails = setExercises.map {
            val validatedDetails = ExerciseDetailsType.validateSetDetails(it.details)
            val jsonDetails = convertDataToJson(validatedDetails)
            Pair(it.exerciseId, jsonDetails)
        }

        return transactionManager.run {
            val setRepo = it.setRepo

            val sets = setRepo.getSetByExercises(setExercises.map { it.exerciseId })

            sets.forEach { set ->
                val validSet = jsonDetails.all { pair ->
                    val (exerciseId, details) = pair
                    setRepo.validateSetExerciseDetails(set, exerciseId, details)
                }

                if (validSet) {
                    return@run set
                }
            }

            return@run null
        }
    }

    fun getSets(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        type: SetType?,
        name: String?,
        favorite: Boolean
    ): Pair<List<TrainerSet>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(name, "Name must be a non-empty string.") { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val setRepo = it.setRepo

            return@run if (favorite) {
                val sets = setRepo.getFavoriteSets(trainerId, skip ?: 0, limit, type, name)
                    .map { set ->
                        val isFavorite = setRepo.isSetFavorite(trainerId, set.id)
                        TrainerSet(set.id, set.name, set.notes, set.type, isFavorite)
                    }
                val totalSets = setRepo.getTotalFavoriteSets(trainerId, type, name)

                Pair(sets, totalSets)
            } else {
                val sets = setRepo.getSets(trainerId, skip ?: 0, limit, type, name)
                    .map { set ->
                        val isFavorite = setRepo.isSetFavorite(trainerId, set.id)
                        TrainerSet(set.id, set.name, set.notes, set.type, isFavorite)
                    }
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

        return transactionManager.runWithLevel(TransactionIsolationLevel.SERIALIZABLE) {
            val workoutRepo = it.workoutRepo
            val setRepo = it.setRepo
            val trainerRepo = it.trainerRepo

            val workoutId = it.createWorkout(trainerId, name, description, muscleGroup)

            sets.forEachIndexed { index, setId ->
                setRepo.getSet(trainerId, setId)
                    ?: throw TrainerError.ResourceNotFoundError

                workoutRepo.associateSetToWorkout(index + 1, setId, workoutId)
            }

            trainerRepo.associateTrainerToWorkout(trainerId, workoutId)

            return@runWithLevel workoutId
        }
    }

    fun searchWorkout(
        sets: List<Int>
    ): Int? =
        transactionManager.run {
            val workoutRepo = it.workoutRepo
            return@run workoutRepo.getWorkoutBySets(sets)
        }

    fun getWorkouts(
        trainerId: UUID,
        skip: Int?,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        favorite: Boolean
    ): Pair<List<TrainerWorkout>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(name, "Name must be a non-empty string.") { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val workoutRepo = it.workoutRepo

            return@run if (favorite) {
                val workouts = workoutRepo.getFavoriteWorkouts(trainerId, skip ?: 0, limit, name, muscleGroup)
                    .map { workout ->
                        val isFavorite = workoutRepo.isWorkoutFavorite(trainerId, workout.id)
                        TrainerWorkout(workout.id, workout.name, workout.description, workout.muscleGroup, isFavorite)
                    }
                val totalWorkouts = workoutRepo.getTotalFavoriteWorkouts(trainerId, name, muscleGroup)

                return@run Pair(workouts, totalWorkouts)
            } else {
                val workouts = workoutRepo.getWorkouts(trainerId, skip ?: 0, limit, name, muscleGroup)
                    .map { workout ->
                        val isFavorite = workoutRepo.isWorkoutFavorite(trainerId, workout.id)
                        TrainerWorkout(workout.id, workout.name, workout.description, workout.muscleGroup, isFavorite)
                    }
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
                val set = setRepo.getWorkoutSet(workoutId, setId) ?: return@mapNotNull null
                val exercises = setRepo.getSetExercises(setId)
                WorkoutSetDetails(set, exercises)
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
        location: String?,
        type: SessionType,
        notes: String?
    ): Int {
        require(beginDate.after(Date())) { "Begin date must be after the current date." }

        Validators.validate(
            Validators.ValidationRequest(notes, "Notes must not be empty.") { (it as String).isNotEmpty() }
        )

        validateSessionDataLocation(beginDate, endDate, location, type)

        return transactionManager.run {
            val traineeRepo = it.traineeRepo
            val trainerRepo = it.trainerRepo
            val sessionRepo = it.sessionRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val sessionId = sessionRepo.createSession(
                traineeId,
                workoutId,
                beginDate,
                endDate,
                location,
                type,
                notes
            )

            if (type == SessionType.TRAINER_GUIDED) {
                trainerRepo.associateSessionToTrainer(trainerId, sessionId)
            }

            return@run sessionId
        }
    }

    fun getSessions(
        trainerId: UUID,
        skip: Int?,
        limit: Int?
    ): Pair<List<TrainerSession>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 }
        )

        return transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessions = sessionRepo.getTrainerSessions(trainerId, skip ?: 0, limit)
            val totalSessions = sessionRepo.getTotalTrainerSessions(trainerId)

            return@run Pair(sessions, totalSessions)
        }
    }

    fun getTraineeSessions(
        trainerId: UUID,
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
            val traineeRepo = it.traineeRepo

            val traineeTrainerId = traineeRepo.getTrainerAssigned(traineeId)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val sessions = sessionRepo.getTraineeSessions(traineeId, skip ?: 0, limit, sessionType)
            val totalSessions = sessionRepo.getTotalTraineeSessions(traineeId, sessionType)

            return@run Pair(sessions, totalSessions)
        }
    }

    fun getSessionDetails(
        trainerId: UUID,
        sessionId: Int
    ): Pair<TrainerSessionDetails, List<SessionFeedback>> =
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val traineeTrainerId = it.traineeRepo.getTrainerAssigned(sessionTrainee)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val sessionDetails = sessionRepo.getTrainerSessionDetails(sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            val feedback = sessionRepo.getSessionFeedbacks(sessionId)

            return@run Pair(sessionDetails, feedback)
        }

//  todo: send mail to trainee
    fun editSession(
        trainerId: UUID,
        sessionId: Int,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        location: String?,
        type: SessionType,
        notes: String?
    ) {
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val traineeTrainerId = it.traineeRepo.getTrainerAssigned(sessionTrainee)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val session = sessionRepo.getSessionDetails(sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            require(isCurrentDate24BeforeDate(session.beginDate)) { "Session can be edited only 24 hours before the begin date." }
            require(beginDate.after(Date())) { "Begin date must be after the current date." }

            validateSessionDataLocation(beginDate, endDate, location, type)

            sessionRepo.updateSession(sessionId, workoutId, beginDate, endDate, location, type, notes)
        }
    }

    fun cancelSession(trainerId: UUID, sessionId: Int, reason: String?) {
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val traineeTrainerId = it.traineeRepo.getTrainerAssigned(sessionTrainee)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            val session = sessionRepo.getSessionDetails(sessionId)
                ?: throw TrainerError.ResourceNotFoundError

            require(isCurrentDate24BeforeDate(session.beginDate)) { "Session can be canceled only 24 hours before the begin date." }

            sessionRepo.cancelSession(sessionId, Source.TRAINER, reason)
        }
    }

    fun createSessionFeedback(
        trainerId: UUID,
        sessionId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(sessionId)
                ?: throw TrainerError.ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val traineeTrainerId = it.traineeRepo.getTrainerAssigned(sessionTrainee)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            require(requestDate.after(session.beginDate) && session.type == SessionType.TRAINER_GUIDED) { "Feedback can only be given after the session has started and for trainer guided sessions." }

            sessionRepo.createFeedback(Source.TRAINER, feedback, requestDate).also { feedbackId ->
                sessionRepo.createSessionFeedback(feedbackId, session.id)
            }
        }
    }

    fun editSessionFeedback(
        trainerId: UUID,
        sessionId: Int,
        feedbackId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(sessionId)
                ?: throw TrainerError.ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val traineeTrainerId = it.traineeRepo.getTrainerAssigned(sessionTrainee)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            require(requestDate.after(session.beginDate) && session.type == SessionType.TRAINER_GUIDED) { "Feedback can only be given after the session has started and for trainer guided sessions." }

            sessionRepo.getSessionFeedback(sessionId, feedbackId)
                ?: throw TrainerError.ResourceNotFoundError

            sessionRepo.editFeedback(sessionId, feedback, requestDate)
        }
    }

    fun createSessionSetFeedback(
        trainerId: UUID,
        sessionId: Int,
        setOrderId: Int,
        setId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(sessionId)
                ?: throw TrainerError.ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val traineeTrainerId = it.traineeRepo.getTrainerAssigned(sessionTrainee)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            require(requestDate.after(session.beginDate) && session.type == SessionType.TRAINER_GUIDED) { "Feedback can only be given after the session has started and for trainer guided sessions." }
            require(sessionRepo.validateSessionSet(sessionId, setOrderId, setId)) { "Set must be part of the session." }

            val feedbackId = sessionRepo.createFeedback(Source.TRAINER, feedback, requestDate)
            sessionRepo.createSessionSetFeedback(feedbackId, session.id, session.workoutId, setOrderId, setId)
        }
    }

    fun editSessionSetFeedback(
        trainerId: UUID,
        sessionId: Int,
        setOrderId: Int,
        setId: Int,
        feedbackId: Int,
        feedback: String
    ) {
        val requestDate = Date()
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            val session = sessionRepo.getSessionDetails(sessionId)
                ?: throw TrainerError.ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val traineeTrainerId = it.traineeRepo.getTrainerAssigned(sessionTrainee)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            require(requestDate.after(session.beginDate) && session.type == SessionType.TRAINER_GUIDED) { "Feedback can only be given after the session has started and for trainer guided sessions." }
            require(sessionRepo.validateSessionSet(sessionId, setOrderId, setId)) { "Set must be part of the session." }

            sessionRepo.getSetSessionFeedback(feedbackId, sessionId, session.workoutId, setOrderId, setId)
                ?: throw TrainerError.ResourceNotFoundError

            sessionRepo.editFeedback(sessionId, feedback, requestDate)
        }
    }

    fun getSetSessionFeedbacks(trainerId: UUID, sessionId: Int): List<SetSessionFeedback> =
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            sessionRepo.getSessionDetails(sessionId)
                ?: throw TrainerError.ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val traineeTrainerId = it.traineeRepo.getTrainerAssigned(sessionTrainee)
                ?: throw TraineeError.TraineeNotAssignedError

            if (traineeTrainerId != trainerId) {
                throw TrainerError.TraineeNotAssignedToTrainerError
            }

            sessionRepo.getSetSessionFeedbacks(sessionId)
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

    private fun validateSessionDataLocation(
        beginDate: Date,
        endDate: Date?,
        location: String?,
        type: SessionType
    ) {
        if (type == SessionType.TRAINER_GUIDED) {
            requireNotNull(endDate) { "End date must be provided for predefined sessions." }
            require(endDate.after(beginDate)) { "End date must be after the begin date." }
            requireNotNull(location) { "Location must be provided for predefined sessions." }
            require(location.isNotEmpty()) { "Location must not be empty." }
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
            setRepo.createSet(name, notes, setType)
        } else {
            val lastSetNameId = setRepo.getLastSetNameId(trainerId) ?: 0
            val nextSetName = "Set #${lastSetNameId + 1}"
            setRepo.createSet(nextSetName, notes, setType)
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
            workoutRepo.createWorkout(name, description, muscleGroup)
        } else {
            val lastWorkoutNameId = workoutRepo.getLastWorkoutNameId(trainerId)
            val nextWorkoutName = "Workout #${lastWorkoutNameId + 1}"
            workoutRepo.createWorkout(nextWorkoutName, description, muscleGroup)
        }
}
