package pt.isel.leic.ptgest.http.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
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
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.model.common.request.CancelSessionRequest
import pt.isel.leic.ptgest.http.model.common.request.CreateFeedbackRequest
import pt.isel.leic.ptgest.http.model.common.response.GetExerciseDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.GetReportDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.GetSetDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.GetSetSessionFeedbacks
import pt.isel.leic.ptgest.http.model.common.response.GetTraineeDataDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.GetWorkoutDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.ListResponse
import pt.isel.leic.ptgest.http.model.trainee.response.GetSessionDetailsResponse
import pt.isel.leic.ptgest.http.utils.AuthenticationRequired
import pt.isel.leic.ptgest.services.TraineeService
import java.util.Date

@RestController
@RequestMapping(Uris.Trainee.PREFIX)
@AuthenticationRequired(Role.TRAINEE)
class TraineeController(
    private val traineeService: TraineeService
) {
    @GetMapping(Uris.Trainee.GET_REPORTS)
    fun getReports(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (reports, total) = traineeService.getReports(authenticatedUser.id, skip, limit)

        return HttpResponse.ok(
            message = "Reports retrieved successfully.",
            details = ListResponse(reports, total)
        )
    }

    @GetMapping(Uris.Trainee.GET_REPORT_DETAILS)
    fun getReportDetails(
        @PathVariable reportId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val reportDetails = traineeService.getReportDetails(
            authenticatedUser.id,
            reportId
        )

        return HttpResponse.ok(
            message = "Report details retrieved successfully.",
            details = GetReportDetailsResponse(reportDetails)
        )
    }

    @GetMapping(Uris.Trainee.GET_TRAINEE_DATA_HISTORY)
    fun getTraineeDataHistory(
        @RequestParam(defaultValue = "DESC") order: Order,
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (traineeData, total) = traineeService.getTraineeDataHistory(
            authenticatedUser.id,
            order,
            skip,
            limit
        )

        return HttpResponse.ok(
            message = "Trainee data history retrieved successfully.",
            details = ListResponse(traineeData, total)
        )
    }

    @GetMapping(Uris.Trainee.GET_TRAINEE_DATA_DETAILS)
    fun getTraineeDataDetails(
        @PathVariable dataId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val traineeDataDetails = traineeService.getTraineeDataDetails(
            authenticatedUser.id,
            dataId
        )

        return HttpResponse.ok(
            message = "Trainee data details retrieved successfully.",
            details = GetTraineeDataDetailsResponse(traineeDataDetails)
        )
    }

    @GetMapping(Uris.Exercise.GET_EXERCISE_DETAILS)
    fun getExerciseDetails(
        @PathVariable exerciseId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exerciseDetails =
            traineeService.getExerciseDetails(
                authenticatedUser.id,
                exerciseId
            )

        return HttpResponse.ok(
            message = "Exercise details retrieved successfully.",
            details = GetExerciseDetailsResponse(exerciseDetails)
        )
    }

    @GetMapping(Uris.Set.GET_SET_DETAILS)
    fun getSetDetails(
        @PathVariable setId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val setDetails = traineeService.getSetDetails(
            authenticatedUser.id,
            setId
        )

        return HttpResponse.ok(
            message = "Set details retrieved successfully.",
            details = GetSetDetailsResponse(setDetails)
        )
    }

    @GetMapping(Uris.Workout.GET_WORKOUT_DETAILS)
    fun getWorkoutDetails(
        @PathVariable workoutId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val workoutDetails = traineeService.getWorkoutDetails(
            authenticatedUser.id,
            workoutId
        )

        return HttpResponse.ok(
            message = "Workout details retrieved successfully.",
            details = GetWorkoutDetailsResponse(workoutDetails)
        )
    }

    @GetMapping(Uris.Session.GET_SESSIONS)
    fun getSessions(
        @RequestParam sessionType: SessionType?,
        @RequestParam date: Date?,
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (sessions, total) = traineeService.getSessions(
            authenticatedUser.id,
            sessionType,
            date,
            skip,
            limit
        )

        return HttpResponse.ok(
            message = "Sessions retrieved successfully.",
            details = ListResponse(sessions, total)
        )
    }

    @GetMapping(Uris.Session.GET_SESSION_DETAILS)
    fun getSessionDetails(
        @PathVariable sessionId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (sessionDetails, feedbacks) = traineeService.getSessionDetails(
            authenticatedUser.id,
            sessionId
        )

        return HttpResponse.ok(
            message = "Session details retrieved successfully.",
            details = GetSessionDetailsResponse(sessionDetails, feedbacks)
        )
    }

    @PostMapping(Uris.Session.CANCEL_SESSION)
    fun cancelSession(
        @PathVariable sessionId: Int,
        @RequestBody cancelReason: CancelSessionRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        traineeService.cancelSession(authenticatedUser.id, sessionId, cancelReason.reason)

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
        traineeService.createSessionFeedback(
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
        traineeService.editSessionFeedback(
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
        traineeService.createSessionSetFeedback(
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
        traineeService.editSessionSetFeedback(
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
        val setSessionFeedbacks = traineeService.getSetSessionFeedbacks(
            authenticatedUser.id,
            sessionId
        )

        return HttpResponse.ok(
            message = "Set session feedbacks retrieved successfully.",
            details = GetSetSessionFeedbacks(setSessionFeedbacks)
        )
    }
}
