package pt.isel.leic.ptgest

import pt.isel.leic.ptgest.domain.common.model.DevInfo

object ServerConfig {

    const val VERSION = "0.0.1"

    private const val KEY_DB_URL = "DB_HOST"

    val developers = listOf(
        DevInfo("Daniel Sousa", "a48642@alunos.isel.pt"),
        DevInfo("Pedro Macedo", "a49471@alunos.isel.pt")
    )

    val dbUrl: String
        get() = System.getenv(KEY_DB_URL)
            ?: throw IllegalStateException("Missing environment variable $KEY_DB_URL")

    val secret: String
        get() = System.getenv("SECRET")
            ?: throw IllegalStateException("Missing environment variable SECRET")

    val mailUsername: String
        get() = System.getenv("MAIL_USERNAME")
            ?: throw IllegalStateException("Missing environment variable MAIL_USERNAME")

    val mailPassword: String
        get() = System.getenv("MAIL_PASSWORD")
            ?: throw IllegalStateException("Missing environment variable MAIL_PASSWORD")
}
