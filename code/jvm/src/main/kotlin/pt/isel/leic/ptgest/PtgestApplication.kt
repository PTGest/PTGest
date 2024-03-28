package pt.isel.leic.ptgest

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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
}

fun main(args: Array<String>) {
    runApplication<PtgestApplication>(*args)
}
