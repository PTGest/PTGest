package pt.isel.leic.ptgest.http.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.model.common.request.CreateExerciseRequest
import pt.isel.leic.ptgest.http.model.common.response.CreateResourceResponse
import pt.isel.leic.ptgest.http.model.common.response.GetExerciseDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.GetSetDetails
import pt.isel.leic.ptgest.http.model.common.response.GetTraineeDataDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.GetWorkoutDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.ListResponse
import pt.isel.leic.ptgest.http.model.trainer.request.AddTraineeDataRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CancelSessionRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CreateFeedbackRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CreateReportRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CreateSessionRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CreateSetRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CreateWorkoutRequest
import pt.isel.leic.ptgest.http.model.trainer.request.EditReportRequest
import pt.isel.leic.ptgest.http.model.trainer.response.GetReportDetailsResponse
import pt.isel.leic.ptgest.http.model.trainer.response.GetSessionDetails
import pt.isel.leic.ptgest.http.model.trainer.response.GetSetSessionFeedbacks
import pt.isel.leic.ptgest.http.utils.AuthenticationRequired
import pt.isel.leic.ptgest.services.trainer.TrainerService
import java.util.Date
import java.util.UUID

@RestController
@RequestMapping(Uris.Trainer.PREFIX)
@AuthenticationRequired(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER)
class TrainerController(
    private val trainerService: TrainerService
) {
    @GetMapping(Uris.Trainer.TRAINEES)
    fun getTrainerTrainees(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam gender: Gender?,
        @RequestParam name: String?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (trainees, total) = trainerService.getTrainerTrainees(
            authenticatedUser.id,
            skip,
            limit,
            gender,
            name?.trim()
        )

        return HttpResponse.ok(
            message = "Company trainees retrieved successfully.",
            details = ListResponse(trainees, total)
        )
    }

    //  Trainee data related endpoints
    @PostMapping(Uris.TraineeData.ADD_TRAINEE_DATA)
    fun addTraineeData(
        @PathVariable traineeId: UUID,
        @RequestBody traineeData: AddTraineeDataRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val dataId = trainerService.addTraineeData(
            authenticatedUser.id,
            traineeId,
            traineeData.gender,
            traineeData.weight,
            traineeData.height,
            traineeData.bodyCircumferences,
            traineeData.bodyFatPercentage,
            traineeData.skinFold
        )

        return HttpResponse.created(
            message = "Trainee data added successfully.",
            details = CreateResourceResponse(dataId)
        )
    }

    @GetMapping(Uris.TraineeData.GET_TRAINEE_DATA_HISTORY)
    fun getTraineeDataHistory(
        @PathVariable traineeId: UUID,
        @RequestParam order: Order?,
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (traineeData, total) = trainerService.getTraineeDataHistory(
            authenticatedUser.id,
            traineeId,
            order,
            skip,
            limit
        )

        return HttpResponse.ok(
            message = "Trainee data retrieved successfully.",
            details = ListResponse(traineeData, total)
        )
    }

    @GetMapping(Uris.TraineeData.GET_TRAINEE_DATA_DETAILS)
    fun getTraineeDataDetails(
        @PathVariable traineeId: UUID,
        @PathVariable dataId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val traineeDataDetails = trainerService.getTraineeDataDetails(
            authenticatedUser.id,
            traineeId,
            dataId
        )

        return HttpResponse.ok(
            message = "Trainee data details retrieved successfully.",
            details = GetTraineeDataDetailsResponse(traineeDataDetails)
        )
    }

    //  Report related endpoints
    @PostMapping(Uris.Report.CREATE_REPORT)
    fun createReport(
        @PathVariable traineeId: UUID,
        @Valid @RequestBody
        reportDetails: CreateReportRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val reportId = trainerService.createReport(
            authenticatedUser.id,
            traineeId,
            reportDetails.report,
            reportDetails.visibility
        )

        return HttpResponse.created(
            message = "Report created successfully.",
            details = CreateResourceResponse(reportId)
        )
    }

    @GetMapping(Uris.Report.GET_REPORTS)
    fun getReports(
        @PathVariable traineeId: UUID,
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (reports, total) = trainerService.getReports(
            authenticatedUser.id,
            traineeId,
            skip,
            limit
        )

        return HttpResponse.ok(
            message = "Reports retrieved successfully.",
            details = ListResponse(reports, total)
        )
    }

    @GetMapping(Uris.Report.GET_REPORT_DETAILS)
    fun getReportDetails(
        @PathVariable traineeId: UUID,
        @PathVariable reportId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val reportDetails = trainerService.getReportDetails(
            authenticatedUser.id,
            traineeId,
            reportId
        )

        return HttpResponse.ok(
            message = "Report details retrieved successfully.",
            details = GetReportDetailsResponse(reportDetails)
        )
    }

    @PutMapping(Uris.Report.EDIT_REPORT)
    fun editReport(
        @PathVariable traineeId: UUID,
        @PathVariable reportId: Int,
        @Valid @RequestBody
        reportDetails: EditReportRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.editReport(
            authenticatedUser.id,
            traineeId,
            reportId,
            reportDetails.report,
            reportDetails.visibility
        )

        return HttpResponse.ok(
            message = "Report edited successfully."
        )
    }

    @DeleteMapping(Uris.Report.DELETE_REPORT)
    fun deleteReport(
        @PathVariable traineeId: UUID,
        @PathVariable reportId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.deleteReport(
            authenticatedUser.id,
            traineeId,
            reportId
        )

        return HttpResponse.ok(
            message = "Report deleted successfully."
        )
    }

    //  Exercise related endpoints
    @PostMapping(Uris.Exercise.CREATE_CUSTOM_EXERCISE)
    fun createCustomExercise(
        @Valid @RequestBody
        exerciseDetails: CreateExerciseRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exerciseId = trainerService.createCustomExercise(
            authenticatedUser.id,
            exerciseDetails.name,
            exerciseDetails.description,
            exerciseDetails.muscleGroup,
            exerciseDetails.modality,
            exerciseDetails.ref
        )

        return HttpResponse.created(
            message = "Custom exercise created successfully.",
            details = CreateResourceResponse(exerciseId)
        )
    }

    @GetMapping(Uris.Exercise.GET_EXERCISES)
    fun getExercises(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam name: String?,
        @RequestParam muscleGroup: MuscleGroup?,
        @RequestParam modality: Modality?,
        @RequestParam favorite: Boolean?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (exercises, total) = trainerService.getExercises(
            authenticatedUser.id,
            authenticatedUser.role,
            skip,
            limit,
            name?.trim(),
            muscleGroup,
            modality,
            favorite ?: false
        )

        return HttpResponse.ok(
            message = "Exercises retrieved successfully.",
            details = ListResponse(exercises, total)
        )
    }

    @GetMapping(Uris.Exercise.GET_EXERCISE_DETAILS)
    fun getExerciseDetails(
        @PathVariable exerciseId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exerciseDetails = trainerService.getExerciseDetails(
            authenticatedUser.id,
            exerciseId
        )

        return HttpResponse.ok(
            message = "Exercise details retrieved successfully.",
            details = GetExerciseDetailsResponse(exerciseDetails)
        )
    }

    @PostMapping(Uris.Exercise.FAVORITE_EXERCISE)
    fun favoriteExercise(
        @PathVariable exerciseId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.favoriteExercise(
            authenticatedUser.id,
            exerciseId
        )

        return HttpResponse.ok(
            message = "Exercise favorited successfully."
        )
    }

    @DeleteMapping(Uris.Exercise.UNFAVORITE_EXERCISE)
    fun unfavoriteExercise(
        @PathVariable exerciseId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.unfavoriteExercise(
            authenticatedUser.id,
            exerciseId
        )

        return HttpResponse.ok(
            message = "Exercise unfavorited successfully."
        )
    }

    //  Set related endpoints
    @PostMapping(Uris.Set.CREATE_CUSTOM_SET)
    fun createCustomSet(
        @Valid @RequestBody
        setDetails: CreateSetRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val setId = trainerService.searchSet(
            setDetails.type,
            setDetails.setExercises
        )

        return if (setId != null) {
            HttpResponse.ok(
                message = "Custom set already exists.",
                details = CreateResourceResponse(setId)
            )
        } else {
            val newSetId = trainerService.createCustomSet(
                authenticatedUser.id,
                setDetails.name,
                setDetails.notes,
                setDetails.type,
                setDetails.setExercises
            )

            HttpResponse.created(
                message = "Custom set created successfully.",
                details = CreateResourceResponse(newSetId)
            )
        }
    }

    @GetMapping(Uris.Set.GET_SETS)
    fun getSets(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam type: SetType?,
        @RequestParam name: String?,
        @RequestParam favorite: Boolean?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (sets, total) = trainerService.getSets(
            authenticatedUser.id,
            skip,
            limit,
            type,
            name?.trim(),
            favorite ?: false
        )

        return HttpResponse.ok(
            message = "Sets retrieved successfully.",
            details = ListResponse(sets, total)
        )
    }

    @GetMapping(Uris.Set.GET_SET_DETAILS)
    fun getSetDetails(
        @PathVariable setId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val setDetails = trainerService.getSetDetails(
            authenticatedUser.id,
            setId
        )

        return HttpResponse.ok(
            message = "Set details retrieved successfully.",
            details = GetSetDetails(setDetails)
        )
    }

    @PostMapping(Uris.Set.FAVORITE_SET)
    fun favoriteSet(
        @PathVariable setId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.favoriteSet(
            authenticatedUser.id,
            setId
        )

        return HttpResponse.ok(
            message = "Set favorited successfully."
        )
    }

    @DeleteMapping(Uris.Set.UNFAVORITE_SET)
    fun unfavoriteSet(
        @PathVariable setId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.unfavoriteSet(
            authenticatedUser.id,
            setId
        )

        return HttpResponse.ok(
            message = "Set unfavorited successfully."
        )
    }

    //  Workout related endpoints
    @PostMapping(Uris.Workout.CREATE_CUSTOM_WORKOUT)
    fun createCustomWorkout(
        @Valid @RequestBody
        workoutDetails: CreateWorkoutRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val workoutId = trainerService.searchWorkout(
            workoutDetails.sets
        )

        return if (workoutId != null) {
            HttpResponse.ok(
                message = "Custom workout already exists.",
                details = CreateResourceResponse(workoutId)
            )
        } else {
            val newWorkoutId = trainerService.createCustomWorkout(
                authenticatedUser.id,
                workoutDetails.name,
                workoutDetails.description,
                workoutDetails.muscleGroup,
                workoutDetails.sets
            )

            HttpResponse.created(
                message = "Custom workout created successfully.",
                details = CreateResourceResponse(newWorkoutId)
            )
        }
    }

    @GetMapping(Uris.Workout.GET_WORKOUTS)
    fun getWorkouts(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam name: String?,
        @RequestParam muscleGroup: MuscleGroup?,
        @RequestParam favorite: Boolean?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (workouts, total) = trainerService.getWorkouts(
            authenticatedUser.id,
            skip,
            limit,
            name?.trim(),
            muscleGroup,
            favorite ?: false
        )

        return HttpResponse.ok(
            message = "Workouts retrieved successfully.",
            details = ListResponse(workouts, total)
        )
    }

    @GetMapping(Uris.Workout.GET_WORKOUT_DETAILS)
    fun getWorkoutDetails(
        @PathVariable workoutId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val workoutDetails = trainerService.getWorkoutDetails(
            authenticatedUser.id,
            workoutId
        )

        return HttpResponse.ok(
            message = "Workout details retrieved successfully.",
            details = GetWorkoutDetailsResponse(workoutDetails)
        )
    }

    @PostMapping(Uris.Workout.FAVORITE_WORKOUT)
    fun favoriteWorkout(
        @PathVariable workoutId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.favoriteWorkout(
            authenticatedUser.id,
            workoutId
        )

        return HttpResponse.ok(
            message = "Workout favorited successfully."
        )
    }

    @DeleteMapping(Uris.Workout.UNFAVORITE_WORKOUT)
    fun unfavoriteWorkout(
        @PathVariable workoutId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.unfavoriteWorkout(
            authenticatedUser.id,
            workoutId
        )

        return HttpResponse.ok(
            message = "Workout unfavorited successfully."
        )
    }

    //  Session related endpoints
    @PostMapping(Uris.Session.CREATE_SESSION)
    fun createSession(
        @RequestBody sessionDetails: CreateSessionRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val sessionId = trainerService.createSession(
            authenticatedUser.id,
            sessionDetails.traineeId,
            sessionDetails.workoutId,
            sessionDetails.beginDate,
            sessionDetails.endDate,
            sessionDetails.location,
            sessionDetails.type,
            sessionDetails.notes
        )

        return HttpResponse.created(
            message = "Session created successfully.",
            details = CreateResourceResponse(sessionId)
        )
    }

    @GetMapping(Uris.Session.GET_SESSIONS)
    fun getSessions(
        @RequestParam date: Date?,
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (sessions, total) = trainerService.getSessions(
            authenticatedUser.id,
            date,
            skip,
            limit
        )

        return HttpResponse.ok(
            message = "Sessions retrieved successfully.",
            details = ListResponse(sessions, total)
        )
    }

    @GetMapping(Uris.Session.GET_TRAINEE_SESSIONS)
    fun getTraineeSessions(
        @PathVariable traineeId: UUID,
        @RequestParam sessionType: SessionType?,
        @RequestParam date: Date?,
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (sessions, total) = trainerService.getTraineeSessions(
            authenticatedUser.id,
            traineeId,
            sessionType,
            date,
            skip,
            limit
        )

        return HttpResponse.ok(
            message = "Trainee sessions retrieved successfully.",
            details = ListResponse(sessions, total)
        )
    }

    @GetMapping(Uris.Session.GET_SESSION_DETAILS)
    fun getSessionDetails(
        @PathVariable sessionId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (sessionDetails, feedbacks) = trainerService.getSessionDetails(
            authenticatedUser.id,
            sessionId
        )

        return HttpResponse.ok(
            message = "Session details retrieved successfully.",
            details = GetSessionDetails(sessionDetails, feedbacks)
        )
    }

    @PutMapping(Uris.Session.EDIT_SESSION)
    fun editSession(
        @PathVariable sessionId: Int,
        @RequestBody sessionDetails: CreateSessionRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.editSession(
            authenticatedUser.id,
            sessionId,
            sessionDetails.workoutId,
            sessionDetails.beginDate,
            sessionDetails.endDate,
            sessionDetails.location,
            sessionDetails.type,
            sessionDetails.notes
        )

        return HttpResponse.ok(
            message = "Session edited successfully."
        )
    }

    @PostMapping(Uris.Session.CANCEL_SESSION)
    fun cancelSession(
        @PathVariable sessionId: Int,
        @RequestBody cancelReason: CancelSessionRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.cancelSession(
            authenticatedUser.id,
            sessionId,
            cancelReason.reason
        )

        return HttpResponse.ok(
            message = "Session canceled successfully."
        )
    }

    @PostMapping(Uris.Session.CREATE_SESSION_FEEDBACK)
    fun createSessionFeedback(
        @PathVariable sessionId: Int,
        @Valid @RequestBody
        feedbackDetails: CreateFeedbackRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.createSessionFeedback(
            authenticatedUser.id,
            sessionId,
            feedbackDetails.feedback
        )

        return HttpResponse.ok(
            message = "Session feedback created successfully."
        )
    }

    @PutMapping(Uris.Session.EDIT_SESSION_FEEDBACK)
    fun editSessionFeedback(
        @PathVariable sessionId: Int,
        @PathVariable feedbackId: Int,
        @Valid @RequestBody
        feedbackDetails: CreateFeedbackRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.editSessionFeedback(
            authenticatedUser.id,
            sessionId,
            feedbackId,
            feedbackDetails.feedback
        )

        return HttpResponse.ok(
            message = "Session feedback edited successfully."
        )
    }

    @PostMapping(Uris.Session.CREATE_SESSION_SET_FEEDBACK)
    fun createSessionSetFeedback(
        @PathVariable sessionId: Int,
        @PathVariable setOrderId: Int,
        @PathVariable setId: Int,
        @Valid @RequestBody
        feedbackDetails: CreateFeedbackRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.createSessionSetFeedback(
            authenticatedUser.id,
            sessionId,
            setOrderId,
            setId,
            feedbackDetails.feedback
        )

        return HttpResponse.ok(
            message = "Session set feedback created successfully."
        )
    }

    @PutMapping(Uris.Session.EDIT_SESSION_SET_FEEDBACK)
    fun editSessionSetFeedback(
        @PathVariable sessionId: Int,
        @PathVariable setOrderId: Int,
        @PathVariable setId: Int,
        @PathVariable feedbackId: Int,
        @Valid @RequestBody
        feedbackDetails: CreateFeedbackRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.editSessionSetFeedback(
            authenticatedUser.id,
            sessionId,
            setOrderId,
            setId,
            feedbackId,
            feedbackDetails.feedback
        )

        return HttpResponse.ok(
            message = "Session set feedback edited successfully."
        )
    }

    @GetMapping(Uris.Session.GET_SET_SESSION_FEEDBACKS)
    fun getSetSessionFeedbacks(
        @PathVariable sessionId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val setSessionFeedbacks = trainerService.getSetSessionFeedbacks(
            authenticatedUser.id,
            sessionId
        )

        return HttpResponse.ok(
            message = "Set session feedbacks retrieved successfully.",
            details = GetSetSessionFeedbacks(setSessionFeedbacks)
        )
    }
}
