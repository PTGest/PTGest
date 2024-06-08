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
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.http.model.common.request.CreateExerciseRequest
import pt.isel.leic.ptgest.http.model.common.response.CreateResourceResponse
import pt.isel.leic.ptgest.http.model.common.response.GetExerciseDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.GetExercisesResponse
import pt.isel.leic.ptgest.http.model.common.response.GetSetDetails
import pt.isel.leic.ptgest.http.model.common.response.GetWorkoutDetailsResponse
import pt.isel.leic.ptgest.http.model.trainer.request.AddTraineeDataRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CreateReportRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CreateSessionRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CreateSetRequest
import pt.isel.leic.ptgest.http.model.trainer.request.CreateWorkoutRequest
import pt.isel.leic.ptgest.http.model.trainer.request.EditReportRequest
import pt.isel.leic.ptgest.http.model.trainer.response.GetReportDetailsResponse
import pt.isel.leic.ptgest.http.model.trainer.response.GetReportsResponse
import pt.isel.leic.ptgest.http.model.trainer.response.GetSetsResponse
import pt.isel.leic.ptgest.http.model.trainer.response.GetWorkoutsResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.utils.RequiredRole
import pt.isel.leic.ptgest.services.trainer.TrainerService
import java.util.UUID

@RestController
@RequestMapping(Uris.Trainer.PREFIX)
@RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER)
class TrainerController(
    private val trainerService: TrainerService
) {
//  Trainee data related endpoints
    @PostMapping(Uris.Trainer.TraineeData.ADD_TRAINEE_DATA)
    fun addTraineeData(
    @RequestBody traineeData: AddTraineeDataRequest,
    authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val dataId = trainerService.addTraineeData(
            authenticatedUser.id,
            traineeData.traineeId,
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

//  Report related endpoints
    @PostMapping(Uris.Trainer.Report.CREATE_REPORT)
    fun createReport(
    @Valid @RequestBody
        reportDetails: CreateReportRequest,
    authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val reportId = trainerService.createReport(
            authenticatedUser.id,
            reportDetails.traineeId,
            reportDetails.report,
            reportDetails.visibility
        )

        return HttpResponse.created(
            message = "Report created successfully.",
            details = CreateResourceResponse(reportId)
        )
    }

//  todo: verify if this method
    @GetMapping(Uris.Trainer.Report.GET_REPORTS)
    fun getReports(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam traineeId: UUID?,
        @RequestParam traineeName: String?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (reports, total) = trainerService.getReports(
            authenticatedUser.id,
            skip,
            limit,
            traineeId,
            traineeName?.trim()
        )

        return HttpResponse.ok(
            message = "Reports retrieved successfully.",
            details = GetReportsResponse(reports, total)
        )
    }

    @GetMapping(Uris.Trainer.Report.GET_REPORT_DETAILS)
    fun getReportDetails(
        @PathVariable reportId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val reportDetails = trainerService.getReportDetails(
            authenticatedUser.id,
            reportId
        )

        return HttpResponse.ok(
            message = "Report details retrieved successfully.",
            details = GetReportDetailsResponse(reportDetails)
        )
    }

    @PutMapping(Uris.Trainer.Report.EDIT_REPORT)
    fun editReport(
        @PathVariable reportId: Int,
        @Valid @RequestBody
        reportDetails: EditReportRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.editReport(
            authenticatedUser.id,
            reportId,
            reportDetails.report,
            reportDetails.visibility
        )

        return HttpResponse.ok(
            message = "Report edited successfully."
        )
    }

    @DeleteMapping(Uris.Trainer.Report.DELETE_REPORT)
    fun deleteReport(
        @PathVariable reportId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.deleteReport(
            authenticatedUser.id,
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
            details = GetExercisesResponse(exercises, total)
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

    @DeleteMapping(Uris.Exercise.DELETE_EXERCISE)
    fun deleteExercise(
        @PathVariable exerciseId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.deleteExercise(
            authenticatedUser.id,
            exerciseId
        )

        return HttpResponse.ok(
            message = "Exercise deleted successfully."
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
            details = GetSetsResponse(sets, total)
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

    @DeleteMapping(Uris.Set.DELETE_SET)
    fun deleteSet(
        @PathVariable setId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.deleteSet(
            authenticatedUser.id,
            setId
        )

        return HttpResponse.ok(
            message = "Set deleted successfully."
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

    @DeleteMapping(Uris.Workout.DELETE_WORKOUT)
    fun deleteWorkout(
        @PathVariable workoutId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        trainerService.deleteWorkout(
            authenticatedUser.id,
            workoutId
        )

        return HttpResponse.ok(
            message = "Workout deleted successfully."
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
