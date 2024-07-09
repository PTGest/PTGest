object ServerConfig {
    val dbUrl: String
        get() = System.getenv("DB_HOST")
        ?: throw IllegalStateException("Missing environment variable DB_HOST")

    val mailUsername: String
        get() = System.getenv("MAIL_USERNAME")
        ?: throw IllegalStateException("Missing environment variable MAIL_USERNAME")

    val mailPassword: String
        get() = System.getenv("MAIL_PASSWORD")
        ?: throw IllegalStateException("Missing environment variable MAIL_PASSWORD")
}