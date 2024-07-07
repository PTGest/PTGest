package pt.isel.leic.ptgest

object ServerConfig {

    private const val KEY_DB_URL = "DB_HOST"

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
