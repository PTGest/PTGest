package pt.isel.leic.ptgest.http.controllers.trainee

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.leic.ptgest.domain.common.Role
import pt.isel.leic.ptgest.http.media.Uris
import pt.isel.leic.ptgest.http.utils.RequiredRole

@RestController
@RequestMapping(Uris.PREFIX)
@RequiredRole(Role.TRAINEE)
class TraineeController
