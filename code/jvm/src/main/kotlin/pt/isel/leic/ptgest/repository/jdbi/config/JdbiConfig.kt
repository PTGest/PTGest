package pt.isel.leic.ptgest.repository.jdbi.config

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.leic.ptgest.repository.jdbi.config.mappers.BodyDataMapper
import pt.isel.leic.ptgest.repository.jdbi.config.mappers.DateMapper
import pt.isel.leic.ptgest.repository.jdbi.config.mappers.MuscleGroupListMapper
import pt.isel.leic.ptgest.repository.jdbi.config.mappers.SetExerciseDetailsMapper

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(DateMapper())
    registerColumnMapper(SetExerciseDetailsMapper())
    registerColumnMapper(MuscleGroupListMapper())
    registerColumnMapper(BodyDataMapper())
    return this
}
