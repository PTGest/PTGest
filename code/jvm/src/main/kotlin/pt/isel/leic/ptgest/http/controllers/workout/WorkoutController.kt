package pt.isel.leic.ptgest.http.controllers.workout

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.http.controllers.workout.model.request.CreateCustomExerciseRequest
import pt.isel.leic.ptgest.http.controllers.workout.model.request.CreateCustomSetRequest
import pt.isel.leic.ptgest.http.controllers.workout.model.request.CreateCustomWorkoutRequest
import pt.isel.leic.ptgest.http.controllers.workout.model.response.CreateCustomResourceResponse
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetExerciseDetailsResponse
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetExercisesResponse
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetSetDetails
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetWorkoutDetailsResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.utils.RequiredRole
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.company.CompanyService
import pt.isel.leic.ptgest.services.trainer.TrainerService
import pt.isel.leic.ptgest.services.workout.WorkoutService

@RestController
@RequestMapping(Uris.Trainer.PREFIX)
class WorkoutController(
    private val trainerService: TrainerService,
    private val companyService: CompanyService,
    private val workoutService: WorkoutService
) {
    @PostMapping(Uris.Workout.CREATE_CUSTOM_EXERCISE)
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER, Role.COMPANY)
    fun createCustomExercise(
        @Valid @RequestBody
        exerciseDetails: CreateCustomExerciseRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exerciseId = workoutService.createCustomExercise(
            exerciseDetails.name,
            exerciseDetails.description,
            exerciseDetails.muscleGroup,
            exerciseDetails.modality,
            exerciseDetails.ref
        )

        when (authenticatedUser.role) {
            Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER -> {
                trainerService.associateTrainerToExercise(authenticatedUser.id, exerciseId)
            }
            Role.COMPANY -> {
                companyService.associateCompanyToExercise(authenticatedUser.id, exerciseId)
            }
            else -> throw AuthError.UserAuthenticationError.UnauthorizedRole
        }

        return HttpResponse.created(
            message = "Custom exercise created successfully.",
            details = CreateCustomResourceResponse(exerciseId)
        )
    }

//  TODO: correct to return more details about the exercise
    @GetMapping(Uris.Workout.GET_EXERCISES)
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER, Role.COMPANY)
    fun getExercises(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam name: String?,
        @RequestParam muscleGroup: MuscleGroup?,
        @RequestParam modality: Modality?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exercises = when (authenticatedUser.role) {
            Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER -> {
                trainerService.getExercises(
                    skip,
                    limit,
                    name?.trim(),
                    muscleGroup,
                    modality,
                    authenticatedUser.id,
                    authenticatedUser.role
                )
            }
            Role.COMPANY -> {
                companyService.getExercises(
                    skip,
                    limit,
                    name?.trim(),
                    muscleGroup,
                    modality,
                    authenticatedUser.id
                )
            }
            else -> throw AuthError.UserAuthenticationError.UnauthorizedRole
        }

        return HttpResponse.ok(
            message = "Exercises retrieved successfully.",
            details = GetExercisesResponse(exercises)
        )
    }

//  TODO: check if this operation needs to be authenticated
    @GetMapping(Uris.Workout.GET_EXERCISE_DETAILS)
    fun getExerciseDetails(
        @PathVariable exerciseId: Int
    ): ResponseEntity<*> {
        val exerciseDetails = workoutService.getExerciseDetails(
            exerciseId
        )

        return HttpResponse.ok(
            message = "Exercise details retrieved successfully.",
            details = GetExerciseDetailsResponse(exerciseDetails)
        )
    }

    @PostMapping(Uris.Workout.CREATE_CUSTOM_SET)
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER)
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
            details = CreateCustomResourceResponse(setId)
        )
    }

    @GetMapping(Uris.Workout.GET_SETS)
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER, Role.TRAINEE)
    fun getSets() {
        throw NotImplementedError("Not implemented.")
    }

//  TODO: change to be correctly authenticated now only supports trainers
    @GetMapping(Uris.Workout.GET_SET_DETAILS)
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER, Role.TRAINEE)
    fun getSetDetails(
        @PathVariable setId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        if (authenticatedUser.role == Role.TRAINEE) {
            throw NotImplementedError("Trainee role not implemented.")
        }

        val setDetails = workoutService.getSetDetails(
            authenticatedUser.id,
            setId
        )

        return HttpResponse.ok(
            message = "Set details retrieved successfully.",
            details = GetSetDetails(setDetails)
        )
    }

    @PostMapping(Uris.Workout.CREATE_CUSTOM_WORKOUT)
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER)
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
            details = CreateCustomResourceResponse(workoutId)
        )
    }

    @GetMapping(Uris.Workout.GET_WORKOUTS)
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER, Role.TRAINEE)
    fun getWorkouts() {
        throw NotImplementedError("Not implemented.")
    }

//  TODO: change to be correctly authenticated now only supports trainers
    @GetMapping(Uris.Workout.GET_WORKOUT_DETAILS)
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER, Role.TRAINEE)
    fun getWorkoutDetails(
        @PathVariable workoutId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        if (authenticatedUser.role == Role.TRAINEE) {
            throw NotImplementedError("Trainee role not implemented.")
        }

        val workoutDetails = workoutService.getWorkoutDetails(
            authenticatedUser.id,
            workoutId
        )

        return HttpResponse.ok(
            message = "Workout details retrieved successfully.",
            details = GetWorkoutDetailsResponse(workoutDetails)
        )
    }
}
