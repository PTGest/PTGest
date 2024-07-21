package pt.isel.leic.ptgest.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.common.Source
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
import pt.isel.leic.ptgest.services.errors.AuthError
import pt.isel.leic.ptgest.services.errors.ResourceNotFoundError
import pt.isel.leic.ptgest.services.errors.TrainerError
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.Calendar
import java.util.Date
import java.util.UUID

@Service
class TrainerService(
    private val transactionManager: TransactionManager,
    private val mailService: MailService
) {

    fun getTrainerTrainees(
        trainerId: UUID,
        gender: Gender?,
        name: String?,
        skip: Int?,
        limit: Int?
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

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            val traineeBirthdate = traineeRepo.getTraineeDetails(traineeId)?.birthdate
                ?: throw AuthError.UserAuthenticationError.UserNotFound

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
        order: Order?,
        skip: Int?,
        limit: Int?
    ): Pair<List<TraineeData>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 }
        )

        return transactionManager.run {
            val traineeDataRepo = it.traineeDataRepo

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            val traineeData = traineeDataRepo.getTraineeData(
                traineeId,
                order ?: Order.DESC,
                skip ?: 0,
                limit
            )

            val totalTraineeData = traineeDataRepo.getTotalTraineeData(traineeId)

            return@run Pair(traineeData, totalTraineeData)
        }
    }

    fun getTraineeDataDetails(
        trainerId: UUID,
        traineeId: UUID,
        dataId: Int
    ): TraineeDataDetails =
        transactionManager.run {
            val traineeDataRepo = it.traineeDataRepo

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            return@run traineeDataRepo.getTraineeBodyDataDetails(traineeId, dataId)
                ?: throw ResourceNotFoundError
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
            val trainerRepo = it.trainerRepo
            val reportRepo = it.reportRepo

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            val reportId = reportRepo.createReport(traineeId, requestDate, report, visibility)

            trainerRepo.associateTrainerToReport(trainerId, reportId)
            return@run reportId
        }
    }

    fun getReports(
        trainerId: UUID,
        traineeId: UUID,
        skip: Int?,
        limit: Int?
    ): Pair<List<Report>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 }
        )

        return transactionManager.run {
            val reportRepo = it.reportRepo

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            val reports = reportRepo.getTrainerReports(traineeId, skip ?: 0, limit)
            val totalReports = reportRepo.getTotalTrainerReports(traineeId)

            return@run Pair(reports, totalReports)
        }
    }

    fun getReportDetails(
        trainerId: UUID,
        traineeId: UUID,
        reportId: Int
    ): ReportDetails =
        transactionManager.run {
            val reportRepo = it.reportRepo

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            return@run reportRepo.getReportDetails(traineeId, reportId)
                ?: throw ResourceNotFoundError
        }

    fun editReport(
        trainerId: UUID,
        traineeId: UUID,
        reportId: Int,
        report: String,
        visibility: Boolean
    ) {
        val requestDate = Date()
        transactionManager.run {
            val reportRepo = it.reportRepo

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            reportRepo.getReportDetails(traineeId, reportId)
                ?: throw ResourceNotFoundError

            require(reportRepo.reportBelongsToTrainer(trainerId, reportId)) { "Report does not belong to the trainer." }

            reportRepo.editReport(reportId, requestDate, report, visibility)
        }
    }

    fun deleteReport(trainerId: UUID, traineeId: UUID, reportId: Int) {
        transactionManager.run {
            val reportRepo = it.reportRepo

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            reportRepo.getReportDetails(traineeId, reportId)
                ?: throw ResourceNotFoundError

            require(reportRepo.reportBelongsToTrainer(trainerId, reportId)) { "Report does not belong to the trainer." }

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
        trainerRole: Role,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean,
        skip: Int?,
        limit: Int?
    ): Pair<List<TrainerExercise>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            return@run it.getExercises(
                trainerId,
                name,
                muscleGroup,
                modality,
                isFavorite,
                skip,
                limit
            )
        }
    }

    fun getExerciseDetails(
        trainerId: UUID,
        exerciseId: Int
    ): ExerciseDetails =
        transactionManager.run {
            val exerciseRepo = it.exerciseRepo

            return@run exerciseRepo.getExerciseDetails(exerciseId)
                ?: throw ResourceNotFoundError
        }

    fun favoriteExercise(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val trainerRepo = it.trainerRepo
            val exerciseRepo = it.exerciseRepo

            val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

            exerciseRepo.getTrainerExerciseDetails(trainerId, exerciseId)
                ?: companyId?.let { exerciseRepo.getCompanyExerciseDetails(companyId, exerciseId) }
                ?: throw ResourceNotFoundError

            if (exerciseRepo.isFavoriteExercise(trainerId, exerciseId)) {
                throw TrainerError.ResourceAlreadyFavoriteError
            }

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
                ?: throw ResourceNotFoundError

            if (!exerciseRepo.isFavoriteExercise(trainerId, exerciseId)) {
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

    fun searchSimilarSet(
        trainerId: UUID,
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
            val trainerRepo = it.trainerRepo

            val sets = setRepo.getSetByExercises(setExercises.map { it.exerciseId })

            sets.forEach { set ->
                val validSet = jsonDetails.all { pair ->
                    val (exerciseId, details) = pair
                    setRepo.validateSetExerciseDetails(set, exerciseId, details)
                }
                if (validSet) {
                    if (!setRepo.isSetOwner(trainerId, set)) {
                        trainerRepo.associateTrainerToSet(trainerId, set)
                    }

                    return@run set
                }
            }
            return@run null
        }
    }

    fun getSets(
        trainerId: UUID,
        type: SetType?,
        name: String?,
        isFavorite: Boolean,
        skip: Int?,
        limit: Int?
    ): Pair<List<TrainerSet>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(name, "Name must be a non-empty string.") { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val setRepo = it.setRepo

            val sets = setRepo.getSets(trainerId, name, type, isFavorite, skip ?: 0, limit)
            val totalSets = setRepo.getTotalSets(trainerId, name, type, isFavorite)

            return@run Pair(sets, totalSets)
        }
    }

    fun getSetDetails(
        trainerId: UUID,
        setId: Int
    ): SetDetails =
        transactionManager.run {
            val setRepo = it.setRepo

            val set = setRepo.getSetDetails(setId)
                ?: throw ResourceNotFoundError
            val exercises = setRepo.getSetExercises(setId)

            return@run SetDetails(set, exercises)
        }

    fun favoriteSet(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val setRepo = it.setRepo

            setRepo.getSet(trainerId, exerciseId)
                ?: throw ResourceNotFoundError

            if (setRepo.isSetFavorite(trainerId, exerciseId)) {
                throw TrainerError.ResourceAlreadyFavoriteError
            }

            setRepo.favoriteSet(trainerId, exerciseId)
        }
    }

    fun unfavoriteSet(trainerId: UUID, exerciseId: Int) {
        transactionManager.run {
            val setRepo = it.setRepo

            setRepo.getSet(trainerId, exerciseId)
                ?: throw ResourceNotFoundError

            if (!setRepo.isSetFavorite(trainerId, exerciseId)) {
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
                    ?: throw ResourceNotFoundError

                workoutRepo.associateSetToWorkout(index + 1, setId, workoutId)
            }

            trainerRepo.associateTrainerToWorkout(trainerId, workoutId)

            return@runWithLevel workoutId
        }
    }

    fun searchSimilarWorkout(
        trainerId: UUID,
        sets: List<Int>
    ): Int? =
        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val trainerRepo = it.trainerRepo

            val workout = workoutRepo.getWorkoutBySets(sets)

            if (workout != null) {
                if (!workoutRepo.isWorkoutOwner(trainerId, workout)) {
                    trainerRepo.associateTrainerToWorkout(trainerId, workout)
                }
            }

            return@run workout
        }

    fun getWorkouts(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        isFavorite: Boolean,
        skip: Int?,
        limit: Int?
    ): Pair<List<TrainerWorkout>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(name, "Name must be a non-empty string.") { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val workoutRepo = it.workoutRepo

            val workouts = workoutRepo.getWorkouts(trainerId, name, muscleGroup, isFavorite, skip ?: 0, limit)
            val totalWorkouts = workoutRepo.getTotalWorkouts(trainerId, name, muscleGroup, isFavorite)

            return@run Pair(workouts, totalWorkouts)
        }
    }

    fun getWorkoutDetails(
        trainerId: UUID,
        workoutId: Int
    ): WorkoutDetails =
        transactionManager.run {
            val workoutRepo = it.workoutRepo
            val setRepo = it.setRepo

            val workout = workoutRepo.getWorkoutDetails(workoutId)
                ?: throw ResourceNotFoundError

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
                ?: throw ResourceNotFoundError

            if (workoutRepo.isWorkoutFavorite(trainerId, workoutId)) {
                throw TrainerError.ResourceAlreadyFavoriteError
            }

            workoutRepo.favoriteWorkout(trainerId, workoutId)
        }
    }

    fun unfavoriteWorkout(trainerId: UUID, workoutId: Int) {
        transactionManager.run {
            val workoutRepo = it.workoutRepo

            workoutRepo.getWorkoutDetails(trainerId, workoutId)
                ?: throw ResourceNotFoundError

            if (!workoutRepo.isWorkoutFavorite(trainerId, workoutId)) {
                throw TrainerError.ResourceNotFavoriteError
            }

            workoutRepo.unfavoriteWorkout(trainerId, workoutId)
        }
    }

    //  Session related Services
    fun createTrainerGuidedSession(
        trainerId: UUID,
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        endDate: Date,
        location: String,
        notes: String?
    ): Int {
        require(beginDate.after(Date())) { "Begin date must be after the current date." }
        require(endDate.after(beginDate)) { "End date must be after the begin date." }
        require(location.isNotEmpty()) { "Location must not be empty." }

        Validators.validate(
            Validators.ValidationRequest(notes, "Notes must not be empty.") { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val trainerRepo = it.trainerRepo
            val sessionRepo = it.sessionRepo

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            val sessionId = sessionRepo.createSession(
                traineeId,
                workoutId,
                beginDate,
                endDate,
                location,
                SessionType.TRAINER_GUIDED,
                notes
            )

            trainerRepo.associateSessionToTrainer(trainerId, sessionId)

            return@run sessionId
        }
    }

    fun createPlanBasedSession(
        trainerId: UUID,
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        notes: String?
    ): Int {
        require(beginDate.after(Date())) { "Begin date must be after the current date." }

        Validators.validate(
            Validators.ValidationRequest(notes, "Notes must not be empty.") { (it as String).isNotEmpty() }
        )

        return transactionManager.run {
            val sessionRepo = it.sessionRepo

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            val sessionId = sessionRepo.createSession(
                traineeId,
                workoutId,
                beginDate,
                null,
                null,
                SessionType.PLAN_BASED,
                notes
            )

            return@run sessionId
        }
    }

    fun getSessions(
        trainerId: UUID,
        date: Date?,
        skip: Int?,
        limit: Int?
    ): Pair<List<TrainerSession>, Int> {
        Validators.validate(
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 }
        )

        return transactionManager.run {
            val sessionRepo = it.sessionRepo

            val sessions = sessionRepo.getTrainerSessions(trainerId, date, skip ?: 0, limit)
            val totalSessions = sessionRepo.getTotalTrainerSessions(trainerId, date)

            return@run Pair(sessions, totalSessions)
        }
    }

    fun getTraineeSessions(
        trainerId: UUID,
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

            it.isTrainerAssignedToTrainee(trainerId, traineeId)

            val sessions = sessionRepo.getTraineeSessions(traineeId, sessionType, date, skip ?: 0, limit)
            val totalSessions = sessionRepo.getTotalTraineeSessions(traineeId, sessionType, date)

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

            it.isTrainerAssignedToTrainee(trainerId, sessionTrainee)

            val sessionDetails = sessionRepo.getTrainerSessionDetails(sessionId)
                ?: throw ResourceNotFoundError

            val feedback = sessionRepo.getSessionFeedbacks(sessionId)

            return@run Pair(sessionDetails, feedback)
        }

    fun editTrainerGuidedSession(
        trainerId: UUID,
        sessionId: Int,
        workoutId: Int,
        beginDate: Date,
        endDate: Date,
        location: String,
        notes: String?
    ) {
        require(beginDate.after(Date())) { "Begin date must be after the current date." }
        require(endDate.after(beginDate)) { "End date must be after the begin date." }
        require(location.isNotEmpty()) { "Location must not be empty." }

        val userDetails = transactionManager.run {
            val sessionRepo = it.sessionRepo
            val userRepo = it.userRepo

            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val userDetails = userRepo.getUserDetails(sessionTrainee)
                ?: throw ResourceNotFoundError

            it.isTrainerAssignedToTrainee(trainerId, sessionTrainee)

            val session = sessionRepo.getSessionDetails(sessionId)
                ?: throw ResourceNotFoundError

            require(Validators.isCurrentDate24BeforeDate(session.beginDate)) { "Session can only be edited 24 hours before the begin date." }
            require(beginDate.after(Date())) { "Begin date must be after the current date." }

            sessionRepo.updateSession(sessionId, workoutId, beginDate, endDate, location, SessionType.TRAINER_GUIDED, notes)

            return@run userDetails
        }

        mailService.sendMail(
            userDetails.email,
            "Session Updated",
            "Your session has been updated by your trainer.\n\n" +
                "Begin Date: $beginDate\n" +
                "End Date: ${endDate}\n" +
                "Location: ${location}\n" +
                "Type: ${SessionType.TRAINER_GUIDED.name}\n" +
                notes?.let { "Notes: ${notes}\n" }
        )
    }

    fun editPlanBasedSession(
        trainerId: UUID,
        sessionId: Int,
        workoutId: Int,
        beginDate: Date,
        notes: String?
    ) {
        require(beginDate.after(Date())) { "Begin date must be after the current date." }

        val userDetails = transactionManager.run {
            val sessionRepo = it.sessionRepo
            val userRepo = it.userRepo

            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val userDetails = userRepo.getUserDetails(sessionTrainee)
                ?: throw ResourceNotFoundError

            it.isTrainerAssignedToTrainee(trainerId, sessionTrainee)

            val session = sessionRepo.getSessionDetails(sessionId)
                ?: throw ResourceNotFoundError

            require(Validators.isCurrentDate24BeforeDate(session.beginDate)) { "Session can only be edited 24 hours before the begin date." }
            require(beginDate.after(Date())) { "Begin date must be after the current date." }

            sessionRepo.updateSession(
                sessionId,
                workoutId,
                beginDate,
                null,
                null,
                SessionType.PLAN_BASED,
                notes
            )

            return@run userDetails
        }

        mailService.sendMail(
            userDetails.email,
            "Session Updated",
            "Your session has been updated by your trainer.\n\n" +
                "Begin Date: $beginDate\n" +
                "Type: ${SessionType.PLAN_BASED}\n" +
                notes?.let { "Notes: ${notes}\n" }
        )
    }

    fun cancelSession(trainerId: UUID, sessionId: Int, reason: String?) {
        val userDetails = transactionManager.run {
            val sessionRepo = it.sessionRepo
            val userRepo = it.userRepo

            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            val userDetails = userRepo.getUserDetails(sessionTrainee)
                ?: throw ResourceNotFoundError

            it.isTrainerAssignedToTrainee(trainerId, sessionTrainee)

            sessionRepo.cancelSession(sessionId, Source.TRAINER, reason)

            return@run userDetails
        }

        mailService.sendMail(
            userDetails.email,
            "Session Cancelled",
            "Your session has been cancelled by your trainer.\n\n" +
                reason?.let { "Reason: ${reason}\n" }
        )
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
                ?: throw ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            it.isTrainerAssignedToTrainee(trainerId, sessionTrainee)

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
                ?: throw ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            it.isTrainerAssignedToTrainee(trainerId, sessionTrainee)

            require(requestDate.after(session.beginDate) && session.type == SessionType.TRAINER_GUIDED) { "Feedback can only be given after the session has started and for trainer guided sessions." }

            sessionRepo.getSessionFeedback(sessionId, feedbackId)
                ?: throw ResourceNotFoundError

            sessionRepo.editFeedback(feedbackId, feedback, requestDate)
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
                ?: throw ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            it.isTrainerAssignedToTrainee(trainerId, sessionTrainee)

            require(requestDate.after(session.beginDate) && session.type == SessionType.TRAINER_GUIDED) { "Feedback can only be given after the session has started and for trainer guided sessions." }
            require(sessionRepo.validateSessionSet(sessionId, setId, setOrderId)) { "Set must be part of the session." }

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
                ?: throw ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            it.isTrainerAssignedToTrainee(trainerId, sessionTrainee)

            require(requestDate.after(session.beginDate) && session.type == SessionType.TRAINER_GUIDED) { "Feedback can only be given after the session has started and for trainer guided sessions." }
            require(sessionRepo.validateSessionSet(sessionId, setOrderId, setId)) { "Set must be part of the session." }

            sessionRepo.getSetSessionFeedback(feedbackId, sessionId, setOrderId, setId)
                ?: throw ResourceNotFoundError

            sessionRepo.editFeedback(feedbackId, feedback, requestDate)
        }
    }

    fun getSetSessionFeedbacks(trainerId: UUID, sessionId: Int): List<SetSessionFeedback> =
        transactionManager.run {
            val sessionRepo = it.sessionRepo

            sessionRepo.getSessionDetails(sessionId)
                ?: throw ResourceNotFoundError
            val sessionTrainee = sessionRepo.getSessionTrainee(sessionId)

            it.isTrainerAssignedToTrainee(trainerId, sessionTrainee)

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
                BodyData(weight, height, bodyCircumferences, bodyComposition, null)
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
                BodyData(weight, height, bodyCircumferences, bodyComposition, null)
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
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean,
        skip: Int?,
        limit: Int?
    ): Pair<List<TrainerExercise>, Int> {
        val companyId = trainerRepo.getCompanyAssignedTrainer(trainerId)

        val resultList = mutableListOf<TrainerExercise>()
        var totalExercises = 0

        getTrainerExercises(
            trainerId,
            name,
            muscleGroup,
            modality,
            isFavorite,
            skip,
            limit,
            { resultList += it },
            { totalExercises += it }
        )
        getCompanyExercises(
            companyId,
            trainerId,
            name,
            muscleGroup,
            modality,
            isFavorite,
            skip,
            limit,
            { resultList += it },
            { totalExercises += it }
        )

        return Pair(resultList, totalExercises)
    }

    private fun Transaction.getTrainerExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean,
        skip: Int?,
        limit: Int?,
        addExercises: (List<TrainerExercise>) -> Unit,
        addTotalExercises: (Int) -> Unit
    ) {
        val trainerExercises = exerciseRepo.getTrainerExercises(
            trainerId,
            name,
            muscleGroup,
            modality,
            isFavorite,
            skip ?: 0,
            limit
        )
        val totalTrainerExercises = exerciseRepo.getTotalTrainerExercises(
            trainerId,
            name,
            muscleGroup,
            modality,
            isFavorite
        )

        addExercises(trainerExercises)
        addTotalExercises(totalTrainerExercises)
    }

    private fun Transaction.getCompanyExercises(
        companyId: UUID?,
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        isFavorite: Boolean,
        skip: Int?,
        limit: Int?,
        addExercises: (List<TrainerExercise>) -> Unit,
        addTotalExercises: (Int) -> Unit
    ) {
        companyId?.let {
            val companyExercises = exerciseRepo.getCompanyTrainerExercises(
                companyId,
                trainerId,
                name,
                muscleGroup,
                modality,
                isFavorite,
                skip ?: 0,
                limit
            )
            val totalCompanyExercises = exerciseRepo.getTotalCompanyTrainerExercises(
                companyId,
                trainerId,
                name,
                muscleGroup,
                modality,
                isFavorite
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
            ?: throw ResourceNotFoundError
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
            val lastWorkoutNameId = workoutRepo.getLastWorkoutNameId(trainerId) ?: 0
            val nextWorkoutName = "Workout #${lastWorkoutNameId + 1}"
            workoutRepo.createWorkout(nextWorkoutName, description, muscleGroup)
        }

    private fun Transaction.isTrainerAssignedToTrainee(trainerId: UUID, traineeId: UUID) {
        if (!traineeRepo.isTraineeAssignedToTrainer(traineeId, trainerId)) {
            throw TrainerError.TrainerNotAssignedToTraineeError
        }
    }
}
