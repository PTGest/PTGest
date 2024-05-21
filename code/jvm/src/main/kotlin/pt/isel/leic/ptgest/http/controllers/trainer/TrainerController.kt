package pt.isel.leic.ptgest.http.controllers.trainer

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.utils.RequiredRole
import pt.isel.leic.ptgest.services.trainer.TrainerService

@RestController
@RequestMapping(Uris.Trainer.PREFIX)
@RequiredRole(Role.INDEPENDENT_TRAINER, Role.HIRED_TRAINER)
class TrainerController(
    private val trainerService: TrainerService
) {
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
