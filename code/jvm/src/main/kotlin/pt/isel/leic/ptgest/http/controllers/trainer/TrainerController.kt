package pt.isel.leic.ptgest.http.controllers.trainer

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
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.http.controllers.common.model.request.CreateExerciseRequest
import pt.isel.leic.ptgest.http.controllers.common.model.request.EditExerciseRequest
import pt.isel.leic.ptgest.http.controllers.common.model.response.CreateResourceResponse
import pt.isel.leic.ptgest.http.controllers.common.model.response.GetExerciseDetailsResponse
import pt.isel.leic.ptgest.http.controllers.common.model.response.GetExercisesResponse
import pt.isel.leic.ptgest.http.controllers.common.model.response.GetSetDetails
import pt.isel.leic.ptgest.http.controllers.common.model.response.GetWorkoutDetailsResponse
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.CreateSessionRequest
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.CreateSetRequest
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.CreateWorkoutRequest
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.EditSetRequest
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.EditWorkoutRequest
import pt.isel.leic.ptgest.http.controllers.trainer.model.response.GetSetsResponse
import pt.isel.leic.ptgest.http.controllers.trainer.model.response.GetWorkoutsResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.utils.RequiredRole
import pt.isel.leic.ptgest.services.trainer.TrainerService

@RestController
@RequestMapping(Uris.Trainer.PREFIX)
@RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER)
class TrainerController(
    private val trainerService: TrainerService
) {
    @PostMapping(Uris.Workout.CREATE_CUSTOM_EXERCISE)
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

    @GetMapping(Uris.Workout.GET_EXERCISES)
    fun getExercises(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam name: String?,
        @RequestParam muscleGroup: MuscleGroup?,
        @RequestParam modality: Modality?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (exercises, total) = trainerService.getExercises(
            authenticatedUser.id,
            authenticatedUser.role,
            skip,
            limit,
            name?.trim(),
            muscleGroup,
            modality
        )

        return HttpResponse.ok(
            message = "Exercises retrieved successfully.",
            details = GetExercisesResponse(exercises, total)
        )
    }

    @GetMapping(Uris.Workout.GET_EXERCISE_DETAILS)
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

    @PutMapping(Uris.Workout.EDIT_EXERCISE)
    fun editExercise(
        @PathVariable exerciseId: Int,
        @Valid @RequestBody
        exerciseDetails: EditExerciseRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.editExercise(
            authenticatedUser.id,
            exerciseId,
            exerciseDetails.name,
            exerciseDetails.description,
            exerciseDetails.muscleGroup,
            exerciseDetails.modality,
            exerciseDetails.ref
        )

        return HttpResponse.ok(
            message = "Exercise edited successfully."
        )
    }

    @PostMapping(Uris.Workout.CREATE_CUSTOM_SET)
    fun createCustomSet(
        @Valid @RequestBody
        setDetails: CreateSetRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val setId = trainerService.createCustomSet(
            authenticatedUser.id,
            setDetails.name,
            setDetails.notes,
            setDetails.type,
            setDetails.setExercises
        )

        return HttpResponse.created(
            message = "Custom set created successfully.",
            details = CreateResourceResponse(setId)
        )
    }

    @GetMapping(Uris.Workout.GET_SETS)
    fun getSets(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam type: SetType?,
        @RequestParam name: String?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (sets, total) = trainerService.getSets(
            authenticatedUser.id,
            skip,
            limit,
            type,
            name?.trim()
        )

        return HttpResponse.ok(
            message = "Sets retrieved successfully.",
            details = GetSetsResponse(sets, total)
        )
    }

    @GetMapping(Uris.Workout.GET_SET_DETAILS)
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

    @PutMapping(Uris.Workout.EDIT_SET)
    fun editSet(
        @PathVariable setId: Int,
        @Valid @RequestBody
        setDetails: EditSetRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.editSet(
            authenticatedUser.id,
            setId,
            setDetails.name,
            setDetails.notes,
            setDetails.type,
            setDetails.setExercises
        )

        return HttpResponse.ok(
            message = "Set edited successfully."
        )
    }

    @PostMapping(Uris.Workout.CREATE_CUSTOM_WORKOUT)
    fun createCustomWorkout(
        @Valid @RequestBody
        workoutDetails: CreateWorkoutRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val workoutId = trainerService.createCustomWorkout(
            authenticatedUser.id,
            workoutDetails.name,
            workoutDetails.description,
            workoutDetails.muscleGroup,
            workoutDetails.sets
        )

        return HttpResponse.created(
            message = "Custom workout created successfully.",
            details = CreateResourceResponse(workoutId)
        )
    }

    @GetMapping(Uris.Workout.GET_WORKOUTS)
    fun getWorkouts(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam name: String?,
        @RequestParam muscleGroup: MuscleGroup?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (workouts, total) = trainerService.getWorkouts(
            authenticatedUser.id,
            skip,
            limit,
            name?.trim(),
            muscleGroup
        )

        return HttpResponse.ok(
            message = "Workouts retrieved successfully.",
            details = GetWorkoutsResponse(workouts, total)
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

    @PutMapping(Uris.Workout.EDIT_WORKOUT)
    fun editWorkout(
        @PathVariable workoutId: Int,
        @Valid @RequestBody
        workoutDetails: EditWorkoutRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.editWorkout(
            authenticatedUser.id,
            workoutId,
            workoutDetails.name,
            workoutDetails.description,
            workoutDetails.muscleGroup,
            workoutDetails.sets
        )

        return HttpResponse.ok(
            message = "Workout edited successfully."
        )
    }

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
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError("Not implemented yet.")
    }

    @GetMapping(Uris.Session.GET_SESSION_DETAILS)
    fun getSessionDetails(
        @PathVariable sessionId: String,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError("Not implemented yet.")
    }

    @PutMapping(Uris.Session.EDIT_SESSION)
    fun editSession(
        @PathVariable sessionId: String,
        @RequestBody sessionDetails: CreateSessionRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError("Not implemented yet.")
    }

    //  TODO: check if we should delete the session or just cancel it in the database
    @DeleteMapping(Uris.Session.CANCEL_SESSION)
    fun cancelSession(
        @PathVariable sessionId: String,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError("Not implemented yet.")
    }
}
