package org.isglaz.battlereport

import liquibase.Scope
import liquibase.command.CommandScope
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import java.io.File
import java.sql.DriverManager

/**
 * Base class for tests that need a real database.
 *
 * Starts Postgres in Testcontainers and applies to it exactly the same changelog that will go
 * to production. The schema is not described separately in tests: if a migration is broken, the
 * tests fail here rather than in production.
 *
 * Requires a running Docker.
 */
abstract class DatabaseTestBase {

    companion object {
        /**
         * The migrations directory lives outside the :server module, so the path is built from the
         * repository root: the working directory of the tests is `server/`.
         */
        private val changelogDir: File = File("../migrations/liquibase").canonicalFile

        @JvmStatic
        protected val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:17-alpine")
                .withDatabaseName("battlereport")
                .withUsername("battlereport")
                .withPassword("battlereport")

        @JvmStatic
        @BeforeAll
        fun startDatabase() {
            postgres.start()
            migrate()
        }

        @JvmStatic
        @AfterAll
        fun stopDatabase() {
            postgres.stop()
        }

        /** Runs `liquibase update` with the same changelog the deployment uses. */
        private fun migrate() {
            check(changelogDir.isDirectory) { "Migrations directory not found: $changelogDir" }

            DriverManager.getConnection(postgres.jdbcUrl, postgres.username, postgres.password)
                .use { connection ->
                    val database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(JdbcConnection(connection))

                    // resourceAccessor is taken from the Scope rather than from the command arguments:
                    // otherwise Liquibase looks for the changelog relative to the working directory and fails to find it.
                    Scope.child(
                        Scope.Attr.resourceAccessor.name,
                        DirectoryResourceAccessor(changelogDir),
                    ) {
                        CommandScope("update").apply {
                            addArgumentValue("database", database)
                            addArgumentValue("changelogFile", "db.changelog-master.yaml")
                        }.execute()
                    }
                }
        }

        /** Coordinates of the started database in the form application.conf expects. */
        @JvmStatic
        protected fun r2dbcUrl(): String =
            "r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}"
    }
}
