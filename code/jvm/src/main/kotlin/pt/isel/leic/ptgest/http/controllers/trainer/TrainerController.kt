package pt.isel.leic.ptgest.http.controllers.trainer

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
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.CreateCustomExerciseRequest
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.CreateCustomSetRequest
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.CreateCustomWorkoutRequest
import pt.isel.leic.ptgest.http.controllers.trainer.model.response.CreateCustomWorkoutResponse
import pt.isel.leic.ptgest.http.controllers.trainer.model.response.GetExerciseDetailsResponse
import pt.isel.leic.ptgest.http.controllers.trainer.model.response.GetExercisesResponse
import pt.isel.leic.ptgest.http.controllers.trainer.model.response.GetSetDetails
import pt.isel.leic.ptgest.http.controllers.trainer.model.response.GetWorkoutDetailsResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.services.trainer.TrainerService
import pt.isel.leic.ptgest.services.workout.WorkoutService

@RestController
@RequestMapping(Uris.Trainer.PREFIX)
class TrainerController(
    private val trainerService: TrainerService,
    private val workoutService: WorkoutService
) {

    @PostMapping(Uris.Trainer.CREATE_CUSTOM_EXERCISE)
    fun createCustomExercise(
        @RequestBody exerciseDetails: CreateCustomExerciseRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exerciseId = workoutService.createCustomExercise(
            exerciseDetails.name,
            exerciseDetails.description,
            exerciseDetails.muscleGroup,
            exerciseDetails.modality,
            exerciseDetails.ref
        )

        trainerService.associateTrainerToExercise(authenticatedUser.id, exerciseId)

        return HttpResponse.created(
            message = "Custom exercise created successfully.",
            details = CreateCustomWorkoutResponse(exerciseId)
        )
    }

    @GetMapping(Uris.Trainer.GET_EXERCISES)
    fun getExercises(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam name: String?,
        @RequestParam muscleGroup: MuscleGroup?,
        @RequestParam modality: Modality?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exercises = trainerService.getExercises(
            skip,
            limit,
            name,
            muscleGroup,
            modality,
            authenticatedUser.id,
            authenticatedUser.role
        )

        return HttpResponse.ok(
            message = "Exercises retrieved successfully.",
            details = GetExercisesResponse(exercises)
        )
    }

    @GetMapping(Uris.Trainer.GET_EXERCISE_DETAILS)
    fun getExerciseDetails(
        @PathVariable exerciseId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exerciseDetails = trainerService.getExerciseDetails(
            authenticatedUser.id,
            authenticatedUser.role,
            exerciseId
        )

        return HttpResponse.ok(
            message = "Exercise details retrieved successfully.",
            details = GetExerciseDetailsResponse(exerciseDetails)
        )
    }

    @PostMapping(Uris.Trainer.CREATE_CUSTOM_SET)
    fun createCustomSet(
        @RequestBody setDetails: CreateCustomSetRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val setId = workoutService.createCustomSet(
            authenticatedUser.id,
            authenticatedUser.role,
            setDetails.name,
            setDetails.notes,
            setDetails.type,
            setDetails.setExercises
        )

        return HttpResponse.created(
            message = "Custom set created successfully.",
            details = CreateCustomWorkoutResponse(setId)
        )
    }

    @GetMapping(Uris.Trainer.GET_SET_DETAILS)
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

    @PostMapping(Uris.Trainer.CREATE_CUSTOM_WORKOUT)
    fun createCustomWorkout(
        @RequestBody workoutDetails: CreateCustomWorkoutRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val workoutId = workoutService.createCustomWorkout(
            authenticatedUser.id,
            workoutDetails.name,
            workoutDetails.description,
            workoutDetails.category,
            workoutDetails.sets
        )

        return HttpResponse.created(
            message = "Custom workout created successfully.",
            details = CreateCustomWorkoutResponse(workoutId)
        )
    }

    @GetMapping(Uris.Trainer.GET_WORKOUT_DETAILS)
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

    @PostMapping(Uris.Trainer.CREATE_SESSION)
    fun createSession(
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError("Not implemented yet.")
    }

    @PutMapping(Uris.Trainer.CHANGE_SESSION_DATE)
    fun changeSessionDate(
        @PathVariable sessionId: String,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError("Not implemented yet.")
    }

    //  TODO: check if we should delete the session or just cancel it in the database
    @DeleteMapping(Uris.Trainer.CANCEL_SESSION)
    fun cancelSession(
        @PathVariable sessionId: String,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError("Not implemented yet.")
    }
}
