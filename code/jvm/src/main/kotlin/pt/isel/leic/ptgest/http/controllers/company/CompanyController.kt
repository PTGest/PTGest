package pt.isel.leic.ptgest.http.controllers.company

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.controllers.company.model.response.GetCompanyTrainersResponse
import pt.isel.leic.ptgest.http.controllers.company.model.response.TrainerResponse.Companion.toResponse
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.services.company.CompanyService

@RestController
@RequestMapping(Uris.PREFIX)
class CompanyController(private val service: CompanyService) {

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

    @PostMapping(Uris.Company.ASSIGN_TRAINER_TRAINEE)
    fun assignTrainerToTrainee(
        authenticatedUser: AuthenticatedUser
    ) {
        throw NotImplementedError()
    }

    @PutMapping(Uris.Company.REASSIGN_TRAINER)
    fun reassignTrainer(
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError()
    }
}
