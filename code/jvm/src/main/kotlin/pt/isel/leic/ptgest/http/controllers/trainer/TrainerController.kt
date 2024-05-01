package pt.isel.leic.ptgest.http.controllers.trainer

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.CreateCustomExerciseRequest
import pt.isel.leic.ptgest.http.controllers.trainer.model.request.CreateCustomSetRequest
import pt.isel.leic.ptgest.http.media.HttpResponse
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.services.trainer.TrainerService

// TODO: return link to access the created resource details
@RestController
@RequestMapping(Uris.Trainer.PREFIX)
class TrainerController(private val service: TrainerService) {

    @PostMapping(Uris.Trainer.CREATE_CUSTOM_EXERCISE)
    fun createCustomExercise(
        @RequestBody exerciseDetails: CreateCustomExerciseRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        service.createCustomExercise(
            authenticatedUser.id,
            exerciseDetails.name,
            exerciseDetails.description,
            exerciseDetails.category,
            exerciseDetails.ref
        )

        return HttpResponse.created(
            message = "Custom exercise created successfully."
        )
    }

    @PostMapping(Uris.Trainer.CREATE_CUSTOM_SET)
    fun createCustomSet(
        @RequestBody setDetails: CreateCustomSetRequest,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        service.createCustomSet(
            authenticatedUser.id,
            setDetails.name,
            setDetails.notes,
            setDetails.details
        )

        return HttpResponse.created(
            message = "Custom set created successfully."
        )
    }

    @PostMapping(Uris.Trainer.CREATE_CUSTOM_WORKOUT)
    fun createCustomWorkout(
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        throw NotImplementedError("Not implemented yet.")
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
