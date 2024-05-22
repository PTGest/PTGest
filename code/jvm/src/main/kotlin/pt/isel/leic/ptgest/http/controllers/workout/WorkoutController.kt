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
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.http.controllers.workout.model.request.CreateCustomExerciseRequest
import pt.isel.leic.ptgest.http.controllers.workout.model.request.CreateCustomSetRequest
import pt.isel.leic.ptgest.http.controllers.workout.model.request.CreateCustomWorkoutRequest
import pt.isel.leic.ptgest.http.controllers.workout.model.response.CreateCustomResourceResponse
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetExerciseDetailsResponse
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetExercisesResponse
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetSetDetails
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetSetsResponse
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetWorkoutDetailsResponse
import pt.isel.leic.ptgest.http.controllers.workout.model.response.GetWorkoutsResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.utils.RequiredRole
import pt.isel.leic.ptgest.services.auth.AuthError
import pt.isel.leic.ptgest.services.company.CompanyService
import pt.isel.leic.ptgest.services.trainee.TraineeService
import pt.isel.leic.ptgest.services.trainer.TrainerService
import pt.isel.leic.ptgest.services.workout.WorkoutService

@RestController
@RequestMapping(Uris.Trainer.PREFIX)
class WorkoutController(
    private val companyService: CompanyService,
    private val trainerService: TrainerService,
    private val traineeService: TraineeService,
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
        val (exercises, total) = when (authenticatedUser.role) {
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
            details = GetExercisesResponse(exercises, total)
        )
    }

    @GetMapping(Uris.Workout.GET_EXERCISE_DETAILS)
    fun getExerciseDetails(
        @PathVariable exerciseId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exerciseDetails = when (authenticatedUser.role) {
            Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER -> {
                trainerService.getExerciseDetails(
                    exerciseId,
                    authenticatedUser.id
                )
            }
            Role.COMPANY -> {
                companyService.getExerciseDetails(
                    exerciseId,
                    authenticatedUser.id
                )
            }
            Role.TRAINEE -> {
                traineeService.getExerciseDetails(
                    exerciseId,
                    authenticatedUser.id
                )
            }
        }

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
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER)
    fun getSets(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam type: SetType?,
        @RequestParam name: String?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (sets, total) = workoutService.getSets(
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
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER, Role.TRAINEE)
    fun getSetDetails(
        @PathVariable setId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val setDetails = workoutService.getSetDetails(
            authenticatedUser.id,
            authenticatedUser.role,
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
        @Valid @RequestBody
        workoutDetails: CreateCustomWorkoutRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val workoutId = workoutService.createCustomWorkout(
            authenticatedUser.id,
            workoutDetails.name,
            workoutDetails.description,
            workoutDetails.muscleGroup,
            workoutDetails.sets
        )

        return HttpResponse.created(
            message = "Custom workout created successfully.",
            details = CreateCustomResourceResponse(workoutId)
        )
    }

    @GetMapping(Uris.Workout.GET_WORKOUTS)
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER)
    fun getWorkouts(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam name: String?,
        @RequestParam muscleGroup: MuscleGroup?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (workouts, total) = workoutService.getWorkouts(
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
    @RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER, Role.TRAINEE)
    fun getWorkoutDetails(
        @PathVariable workoutId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val workoutDetails = workoutService.getWorkoutDetails(
            authenticatedUser.id,
            authenticatedUser.role,
            workoutId
        )

        return HttpResponse.ok(
            message = "Workout details retrieved successfully.",
            details = GetWorkoutDetailsResponse(workoutDetails)
        )
    }
}
