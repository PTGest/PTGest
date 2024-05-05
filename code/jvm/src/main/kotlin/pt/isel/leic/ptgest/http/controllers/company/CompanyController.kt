package pt.isel.leic.ptgest.http.controllers.company

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
import pt.isel.leic.ptgest.http.controllers.company.model.request.AssignTrainerRequest
import pt.isel.leic.ptgest.http.controllers.company.model.request.CreateCustomExerciseRequest
import pt.isel.leic.ptgest.http.controllers.company.model.request.ReassignTrainerRequest
import pt.isel.leic.ptgest.http.controllers.company.model.request.UpdateTrainerCapacityRequest
import pt.isel.leic.ptgest.http.controllers.company.model.response.GetCompanyTrainersResponse
import pt.isel.leic.ptgest.http.controllers.company.model.response.TrainerResponse.Companion.toResponse
import pt.isel.leic.ptgest.http.controllers.trainer.model.response.CreateCustomExerciseResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.services.company.CompanyService
import java.util.*

@RestController
@RequestMapping(Uris.Company.PREFIX)
class CompanyController(private val service: CompanyService) {

//  TODO: Add to the response the total number of trainee assigned to the trainer and the capacity
    @GetMapping(Uris.Company.COMPANY_TRAINERS)
    fun getCompanyTrainers(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val trainers = service.getCompanyTrainers(skip, limit, authenticatedUser.id)

        return HttpResponse.ok(
            message = "Company trainers retrieved successfully.",
            details = GetCompanyTrainersResponse(
                trainers = trainers.trainers.map { it.toResponse() },
                total = trainers.total
            )
        )
    }

    @PostMapping(Uris.Company.ASSIGN_TRAINER)
    fun assignTrainerToTrainee(
        @PathVariable traineeId: UUID,
        @RequestBody trainerInfo: AssignTrainerRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        service.assignTrainerToTrainee(trainerInfo.trainerId, traineeId, authenticatedUser.id)

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
        service.reassignTrainer(trainerInfo.newTrainerId, traineeId, authenticatedUser.id)

        return HttpResponse.ok(
            message = "Trainer reassigned to trainee successfully."
        )
    }

    @PutMapping(Uris.Company.UPDATE_TRAINER_CAPACITY)
    fun updateTrainerCapacity(
        @PathVariable trainerId: UUID,
        @RequestParam capacityInfo: UpdateTrainerCapacityRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        service.updateTrainerCapacity(trainerId, authenticatedUser.id, capacityInfo.capacity)

        return HttpResponse.ok(
            message = "Trainer capacity updated successfully to ${authenticatedUser.id}."
        )
    }

    @DeleteMapping(Uris.Company.REMOVE_TRAINER)
    fun removeTrainer(
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError()
    }

    @PostMapping(Uris.Company.CREATE_CUSTOM_EXERCISE)
    fun createCustomExercise(
        @RequestBody exerciseDetails: CreateCustomExerciseRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val exerciseId = service.createCustomExercise(
            authenticatedUser.id,
            exerciseDetails.name,
            exerciseDetails.description,
            exerciseDetails.muscleGroup,
            exerciseDetails.exerciseType,
            exerciseDetails.ref
        )

        return HttpResponse.created(
            message = "Custom exercise created successfully.",
            details = CreateCustomExerciseResponse(exerciseId)
        )
    }
}
