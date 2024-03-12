package pt.isel.leic.ptgest

import pt.isel.leic.ptgest.domain.common.DevInfo

object ServerConfig {

    const val VERSION = "0.0.1"

    private const val KEY_DB_URL = "DB_HOST"

    val developers = listOf(
        DevInfo("Daniel Sousa", "a48642@alunos.isel.pt"),
        DevInfo("Pedro Macedo", "a49471@alunos.isel.pt"),
    )

    val dbUrl: String
        get() = System.getenv(KEY_DB_URL)
            ?: throw IllegalStateException("Missing environment variable $KEY_DB_URL")
}