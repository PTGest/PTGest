package pt.isel.leic.ptgest.http.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.model.common.response.GetExerciseDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.GetSetDetails
import pt.isel.leic.ptgest.http.model.common.response.GetWorkoutDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.ListResponse
import pt.isel.leic.ptgest.http.model.trainee.request.CancelSessionRequest
import pt.isel.leic.ptgest.http.model.trainee.response.GetSessionDetailsResponse
import pt.isel.leic.ptgest.http.utils.RequiredRole
import pt.isel.leic.ptgest.services.trainee.TraineeService

@RestController
@RequestMapping(Uris.Trainee.PREFIX)
@RequiredRole(Role.TRAINEE)
class TraineeController(
    private val traineeService: TraineeService
) {
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
            details = GetSetDetails(setDetails)
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
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam sessionType: SessionType?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (sessions, total) = traineeService.getSessions(
            authenticatedUser.id,
            skip,
            limit,
            sessionType
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
        val sessionDetails = traineeService.getSessionDetails(
            authenticatedUser.id,
            sessionId
        )

        return HttpResponse.ok(
            message = "Session details retrieved successfully.",
            details = GetSessionDetailsResponse(sessionDetails)
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
}
