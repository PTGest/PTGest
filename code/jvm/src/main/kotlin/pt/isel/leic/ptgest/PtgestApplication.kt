package pt.isel.leic.ptgest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.leic.ptgest.domain.auth.Sha256TokenEncoder

@SpringBootApplication
class PtgestApplication {
//	@Bean
//	fun jdbi() = Jdbi.create(
//		PGSimpleDataSource().apply {
//			setURL(ServerConfig.dbUrl)
//			currentSchema = "prod"
//		}
//	).configureWithAppRequirements()

	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()

	@Bean
	fun tokenEncoder() = Sha256TokenEncoder()
}

fun main(args: Array<String>) {
	runApplication<PtgestApplication>(*args)
}
