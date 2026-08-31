package org.isglaz.battlereport

import io.ktor.server.application.Application
import io.ktor.server.application.log
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase

/**
 * Единственная точка подключения к БД.
 *
 * Схему создаёт только Liquibase (каталог `migrations/liquibase`), приложение её не трогает:
 * никаких `SchemaUtils.create` — иначе получаются две расходящиеся версии схемы.
 * Миграции накатываются отдельным шагом деплоя до старта сервера, см. `deploy/migrate.sh`.
 *
 * Координаты БД приходят из `application.conf`, который читает их из переменных окружения.
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
