package pt.isel.leic.ptgest.http.utils

import pt.isel.leic.ptgest.domain.user.Role

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthenticationRequired(vararg val role: Role)
