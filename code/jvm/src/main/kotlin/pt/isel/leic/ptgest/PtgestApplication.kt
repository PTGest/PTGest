package pt.isel.leic.ptgest

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.leic.ptgest.domain.auth.Sha256TokenEncoder
import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
import pt.isel.leic.ptgest.http.pipeline.auth.AuthInterceptor
import pt.isel.leic.ptgest.http.pipeline.auth.AuthenticatedUserResolver
import pt.isel.leic.ptgest.repository.jdbi.config.configureWithAppRequirements

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
    fun tokenEncoder() = Sha256TokenEncoder()

    @Bean
    fun jwtSecret() = JWTSecret(ServerConfig.secret)

    @Bean
    fun javaMailSender() = JavaMailSenderImpl().apply {
        host = "smtp.gmail.com"
        port = 587
        username = ServerConfig.mailUsername
        password = ServerConfig.mailPassword
        javaMailProperties["mail.smtp.auth"] = "true"
        javaMailProperties["mail.smtp.starttls.enable"] = "true"
    }
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

@Configuration
class CorsConfiguration : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(ServerConfig.frontendUrl)
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
    }
}

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger(PtgestApplication::class.java)

    logger.info("Starting application")
    runApplication<PtgestApplication>(*args)
}
