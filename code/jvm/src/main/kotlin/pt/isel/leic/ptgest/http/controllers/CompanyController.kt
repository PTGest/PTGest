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
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.user.Role
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.model.common.request.CreateExerciseRequest
import pt.isel.leic.ptgest.http.model.common.response.CreateResourceResponse
import pt.isel.leic.ptgest.http.model.common.response.GetExerciseDetailsResponse
import pt.isel.leic.ptgest.http.model.common.response.ListResponse
import pt.isel.leic.ptgest.http.model.company.request.AssignTrainerRequest
import pt.isel.leic.ptgest.http.model.company.request.ReassignTrainerRequest
import pt.isel.leic.ptgest.http.model.company.request.UpdateTrainerCapacityRequest
import pt.isel.leic.ptgest.http.utils.AuthenticationRequired
import pt.isel.leic.ptgest.services.CompanyService
import java.util.UUID

@RestController
@RequestMapping(Uris.Company.PREFIX)
@AuthenticationRequired(Role.COMPANY)
class CompanyController(
    private val companyService: CompanyService
) {
    @GetMapping(Uris.Company.TRAINERS)
    fun getCompanyTrainers(
        @RequestParam gender: Gender?,
        @RequestParam(defaultValue = "DESC") availability: Order,
        @RequestParam name: String?,
        @RequestParam excludeTraineeTrainer: UUID?,
        @RequestParam limit: Int?,
        @RequestParam skip: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (trainers, total) = companyService.getCompanyTrainers(
            authenticatedUser.id,
            gender,
            availability,
            name?.trim(),
            excludeTraineeTrainer,
            skip,
            limit
        )

        return HttpResponse.ok(
            message = "Company trainers retrieved successfully.",
            details = ListResponse(trainers, total)
        )
    }

    @GetMapping(Uris.Company.TRAINEES)
    fun getCompanyTrainees(
        @RequestParam gender: Gender?,
        @RequestParam name: String?,
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (trainees, total) = companyService.getCompanyTrainees(
            authenticatedUser.id,
            gender,
            name?.trim(),
            skip,
            limit
        )

        return HttpResponse.ok(
            message = "Company trainees retrieved successfully.",
            details = ListResponse(trainees, total)
        )
    }

    @PostMapping(Uris.Company.ASSIGN_TRAINER)
    fun assignTrainerToTrainee(
        @PathVariable traineeId: UUID,
        @RequestBody trainerInfo: AssignTrainerRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        companyService.assignTrainerToTrainee(authenticatedUser.id, trainerInfo.trainerId, traineeId)

        return HttpResponse.created(
            message = "Trainer assigned to trainee successfully."
        )
    }

    @PutMapping(Uris.Company.REASSIGN_TRAINER)
    fun reassignTrainer(
        @PathVariable traineeId: UUID,
        @RequestBody trainerInfo: ReassignTrainerRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        companyService.reassignTrainer(authenticatedUser.id, trainerInfo.newTrainerId, traineeId)

        return HttpResponse.ok(
            message = "Trainer reassigned to trainee successfully."
        )
    }

    @PutMapping(Uris.Company.UPDATE_TRAINER_CAPACITY)
    fun updateTrainerCapacity(
        @PathVariable trainerId: UUID,
        @RequestBody capacityInfo: UpdateTrainerCapacityRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        companyService.updateTrainerCapacity(authenticatedUser.id, trainerId, capacityInfo.capacity)

        return HttpResponse.ok(
            message = "Trainer capacity updated successfully to ${capacityInfo.capacity}."
        )
    }

    @PostMapping(Uris.Exercise.CREATE_CUSTOM_EXERCISE)
    fun createCustomExercise(
        @Valid @RequestBody
        exerciseDetails: CreateExerciseRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exerciseId = companyService.createCustomExercise(
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
        @RequestParam name: String?,
        @RequestParam muscleGroup: MuscleGroup?,
        @RequestParam modality: Modality?,
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (exercises, total) =
            companyService.getExercises(
                authenticatedUser.id,
                name?.trim(),
                muscleGroup,
                modality,
                skip,
                limit
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
        val exerciseDetails =
            companyService.getExerciseDetails(
                authenticatedUser.id,
                exerciseId
            )

        return HttpResponse.ok(
            message = "Exercise details retrieved successfully.",
            details = GetExerciseDetailsResponse(exerciseDetails)
        )
    }
}
