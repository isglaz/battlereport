plugins {
    `java-library`
}

group = "org.isglaz.battlereport"
version = "1.0.0"

/*
 * Модуль намеренно не содержит Kotlin-кода: это контейнер для SQL-миграций.
 *
 * Две роли:
 *  1. `./gradlew :migrations:update` — прогон миграций по живой БД. Стадия отделена
 *     от сборки и запуска :server, сервер миграции НЕ применяет.
 *  2. resources попадают в jar, поэтому :server может забрать changelog в classpath
 *     тестов и поднять схему в Testcontainers.
 *
 * Liquibase запускается через JavaExec поверх официального CLI, без gradle-плагина:
 * плагин 3.x требует liquibase-core в buildscript classpath и падает на apply.
 */

val liquibaseRuntime: Configuration by configurations.creating

dependencies {
    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.postgresql)
    liquibaseRuntime(libs.picocli)
    liquibaseRuntime(libs.logback)
}

/*
 * Параметры подключения — из -P или переменных окружения, чтобы креды не лежали
 * в репозитории. Локальный дефолт совпадает с БД, которую поднимает docker compose.
 */
fun dbProp(name: String, env: String, default: String): String =
    (findProperty(name) as String?) ?: System.getenv(env) ?: default

val changelogFile = "db/changelog/db.changelog-master.yaml"

// Считываем на этапе конфигурации: внутри doFirst/провайдеров обращение к project
// несовместимо с configuration cache.
val dbUrl = dbProp("db.url", "DB_URL", "jdbc:postgresql://localhost:5432/battlereport")
val dbUsername = dbProp("db.username", "DB_USERNAME", "battlereport")
val dbPassword = dbProp("db.password", "DB_PASSWORD", "battlereport")

/** Общая обвязка для всех liquibase-команд: одна и та же БД, один и тот же changelog. */
fun registerLiquibaseTask(
    name: String,
    command: String,
    description: String,
    extraArgs: List<String> = emptyList(),
) = tasks.register<JavaExec>(name) {
    group = "liquibase"
    this.description = description
    classpath = liquibaseRuntime + sourceSets.main.get().output
    mainClass = "liquibase.integration.commandline.LiquibaseCommandLine"

    args = listOf(
        "--changelog-file=$changelogFile",
        "--url=$dbUrl",
        "--username=$dbUsername",
        "--password=$dbPassword",
        "--log-level=info",
        command,
    ) + extraArgs
}

registerLiquibaseTask("update", "update", "Применяет неприменённые миграции к БД.")
registerLiquibaseTask("status", "status", "Показывает, каких миграций не хватает в БД.")
registerLiquibaseTask("validate", "validate", "Проверяет changelog на дубли id и битые checksum.")
registerLiquibaseTask("updateSql", "update-sql", "Печатает SQL, который применил бы update, ничего не меняя.")
registerLiquibaseTask(
    "rollbackLastRelease",
    "rollback-count",
    "Откатывает N последних changeSet-ов: -ProllbackCount=3.",
    extraArgs = listOf("--count=${findProperty("rollbackCount") ?: "1"}"),
)
