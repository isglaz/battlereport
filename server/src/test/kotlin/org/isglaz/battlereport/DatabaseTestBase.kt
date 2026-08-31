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
 * База для тестов, которым нужна настоящая БД.
 *
 * Поднимает Postgres в Testcontainers и накатывает на него ровно тот же changelog,
 * что уедет в прод. Схема в тестах не описывается отдельно: если миграция сломана,
 * тесты падают здесь, а не на проде.
 *
 * Требует запущенного Docker.
 */
abstract class DatabaseTestBase {

    companion object {
        /**
         * Каталог с миграциями лежит вне модуля :server, поэтому путь строится от корня
         * репозитория: рабочая директория тестов — это `server/`.
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

        /** Прогоняет `liquibase update` тем же changelog-ом, что использует деплой. */
        private fun migrate() {
            check(changelogDir.isDirectory) { "Каталог с миграциями не найден: $changelogDir" }

            DriverManager.getConnection(postgres.jdbcUrl, postgres.username, postgres.password)
                .use { connection ->
                    val database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(JdbcConnection(connection))

                    // resourceAccessor берётся из Scope, а не из аргументов команды:
                    // иначе Liquibase ищет changelog относительно рабочей директории и не находит.
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

        /** Координаты поднятой БД в том виде, в каком их ждёт application.conf. */
        @JvmStatic
        protected fun r2dbcUrl(): String =
            "r2dbc:postgresql://${postgres.host}:${postgres.firstMappedPort}/${postgres.databaseName}"
    }
}
