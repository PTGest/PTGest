package pt.isel.leic.ptgest.http.pipeline.auth

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import pt.isel.leic.ptgest.domain.auth.model.AuthenticatedUser

@Component
class AuthenticatedUserResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AuthenticatedUser::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("No request was found in webRequest")
        return getUserFrom(request) ?: throw IllegalStateException("No user was found in request")
    }

    companion object {
        private const val KEY = "AuthenticatedUserArgumentResolver"

        fun addUserTo(user: AuthenticatedUser, request: HttpServletRequest) {
            return request.setAttribute(KEY, user)
        }

        private fun getUserFrom(request: HttpServletRequest): AuthenticatedUser? {
            return request.getAttribute(KEY)?.let {
                it as? AuthenticatedUser
            }
        }
    }
}
