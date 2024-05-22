package pt.isel.leic.ptgest.repository.jdbi.config

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.leic.ptgest.repository.jdbi.config.mappers.DateMapper
import pt.isel.leic.ptgest.repository.jdbi.config.mappers.ExerciseMapper
import pt.isel.leic.ptgest.repository.jdbi.config.mappers.JsonbMapper
import pt.isel.leic.ptgest.repository.jdbi.config.mappers.WorkoutMapper

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(DateMapper())
    registerColumnMapper(JsonbMapper())
    registerRowMapper(ExerciseMapper())
    registerRowMapper(WorkoutMapper())
    return this
}
