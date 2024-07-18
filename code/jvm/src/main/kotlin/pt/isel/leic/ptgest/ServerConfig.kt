package pt.isel.leic.ptgest

object ServerConfig {
    val dbUrl: String
        get() = System.getenv("DB_HOST")
            ?: throw IllegalStateException("Missing environment variable DB_HOST")

    val secret: String
        get() = System.getenv("SECRET")
            ?: throw IllegalStateException("Missing environment variable SECRET")

    val mailUsername: String
        get() = System.getenv("MAIL_USERNAME")
            ?: throw IllegalStateException("Missing environment variable MAIL_USERNAME")

    val mailPassword: String
        get() = System.getenv("MAIL_PASSWORD")
            ?: throw IllegalStateException("Missing environment variable MAIL_PASSWORD")

    val frontendUrl: String
        get() = System.getenv("FRONTEND_URL")
            ?: "http://localhost:5173"
}
