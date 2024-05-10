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
import pt.isel.leic.ptgest.domain.common.Gender
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.http.controllers.company.model.request.AssignTrainerRequest
import pt.isel.leic.ptgest.http.controllers.company.model.request.CreateCustomExerciseRequest
import pt.isel.leic.ptgest.http.controllers.company.model.request.ReassignTrainerRequest
import pt.isel.leic.ptgest.http.controllers.company.model.request.UpdateTrainerCapacityRequest
import pt.isel.leic.ptgest.http.controllers.company.model.response.GetCompanyTraineesResponse
import pt.isel.leic.ptgest.http.controllers.company.model.response.GetCompanyTrainersResponse
import pt.isel.leic.ptgest.http.controllers.company.model.response.TraineeResponse
import pt.isel.leic.ptgest.http.controllers.company.model.response.TrainerResponse
import pt.isel.leic.ptgest.http.controllers.trainer.model.response.CreateCustomWorkoutResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.services.company.CompanyService
import pt.isel.leic.ptgest.services.workout.WorkoutService
import java.util.UUID

@RestController
@RequestMapping(Uris.Company.PREFIX)
class CompanyController(
    private val companyService: CompanyService,
    private val workoutService: WorkoutService
) {

    @GetMapping(Uris.Company.COMPANY_TRAINERS)
    fun getCompanyTrainers(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam gender: Gender?,
        @RequestParam(defaultValue = "DESC") availability: Order,
        @RequestParam name: String?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val trainers = companyService.getCompanyTrainers(skip, limit, gender, availability, name, authenticatedUser.id)

        return HttpResponse.ok(
            message = "Company trainers retrieved successfully.",
            details = GetCompanyTrainersResponse(
                trainers = trainers.trainers.map { TrainerResponse(it) },
                total = trainers.total
            )
        )
    }

    @GetMapping(Uris.Company.COMPANY_TRAINEES)
    fun getCompanyTrainees(
        @RequestParam skip: Int?,
        @RequestParam limit: Int?,
        @RequestParam gender: Gender?,
        @RequestParam name: String?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val trainees = companyService.getCompanyTrainees(skip, limit, gender, name, authenticatedUser.id)

        return HttpResponse.ok(
            message = "Company trainees retrieved successfully.",
            details = GetCompanyTraineesResponse(
                trainees = trainees.trainees.map { TraineeResponse(it) },
                total = trainees.total
            )
        )
    }

    @PostMapping(Uris.Company.ASSIGN_TRAINER)
    fun assignTrainerToTrainee(
        @PathVariable traineeId: UUID,
        @RequestBody trainerInfo: AssignTrainerRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        companyService.assignTrainerToTrainee(trainerInfo.trainerId, traineeId, authenticatedUser.id)

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
        companyService.reassignTrainer(trainerInfo.newTrainerId, traineeId, authenticatedUser.id)

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
        companyService.updateTrainerCapacity(trainerId, authenticatedUser.id, capacityInfo.capacity)

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
        val exerciseId = workoutService.createCustomExercise(
            exerciseDetails.name,
            exerciseDetails.description,
            exerciseDetails.muscleGroup,
            exerciseDetails.exerciseType,
            exerciseDetails.ref
        )

        companyService.associateCompanyToExercise(authenticatedUser.id, exerciseId)

        return HttpResponse.created(
            message = "Custom exercise created successfully.",
            details = CreateCustomWorkoutResponse(exerciseId)
        )
    }

    @GetMapping(Uris.Company.GET_EXERCISE_DETAILS)
    fun getExerciseDetails(
        @PathVariable exerciseId: Int,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError()
    }
}
