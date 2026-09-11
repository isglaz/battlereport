package org.isglaz.battlereport

import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase

/**
 * The single database connection point.
 *
 * The schema is created by Liquibase only (the `migrations/liquibase` directory), the application
 * never touches it: no `SchemaUtils.create` - otherwise two diverging schema versions appear.
 * Migrations are applied as a separate deployment step before the server starts, see `deploy/migrate.sh`.
 *
 * The database coordinates come from `application.conf`, which reads them from environment variables.
 */
fun Application.configureDatabase() {
    val config = environment.config
    val url = config.property("db.url").getString()

    log.info("Connecting to database at $url")
    R2dbcDatabase.connect(
        url = url,
        user = config.property("db.user").getString(),
        password = config.property("db.password").getString(),
    )
}
