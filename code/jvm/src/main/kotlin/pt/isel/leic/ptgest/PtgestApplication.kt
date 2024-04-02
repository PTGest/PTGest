package pt.isel.leic.ptgest

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
import pt.isel.leic.ptgest.http.pipeline.auth.AuthInterceptor
import pt.isel.leic.ptgest.http.pipeline.auth.AuthenticatedUserResolver
import pt.isel.leic.ptgest.repository.jdbi.configureWithAppRequirements

@SpringBootApplication
class PtgestApplication {
    @Bean
    fun jdbi() = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(ServerConfig.dbUrl)
            currentSchema = "prod"
        }
    ).configureWithAppRequirements()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun jwtSecret() = JWTSecret(ServerConfig.secret)
}

@Configuration
class PipelineConfigurer(
    private val authenticationInterceptor: AuthInterceptor,
    private val authenticatedUserArgumentResolver: AuthenticatedUserResolver
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticatedUserArgumentResolver)
    }
}

fun main(args: Array<String>) {
    runApplication<PtgestApplication>(*args)
}
