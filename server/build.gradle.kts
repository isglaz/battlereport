plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "org.isglaz.battlereport"
version = "1.0.0"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":common"))
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.defaultHeaders)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.openapi)
    implementation(ktorLibs.server.requestValidation)
    implementation(ktorLibs.server.resources)
    implementation(ktorLibs.server.routingOpenapi)
    implementation(ktorLibs.server.statusPages)
    implementation(libs.exposed.core)
    implementation(libs.exposed.r2dbc)
    implementation(libs.logback)
    implementation(libs.postgresql.r2dbc)

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)

    // Интеграционные тесты поднимают Postgres в Testcontainers и накатывают на него
    // ровно те же миграции, что уедут в прод: Liquibase читает changelog прямо из
    // каталога migrations/liquibase, поэтому модуль-обёртка не нужна.
    // Liquibase ходит в БД только через JDBC, отсюда драйвер в тестовых зависимостях.
    testImplementation(libs.postgresql)
    testImplementation(libs.liquibase.core)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junitJupiter)
    testImplementation(libs.testcontainers.postgresql)
}

tasks.test {
    useJUnitPlatform()

    // Docker Desktop 29 отвечает пустым 400 на запросы без версии API, которые шлёт
    // docker-java внутри Testcontainers (демон объявляет min API 1.40). docker-java
    // читает версию из системного свойства api.version, а не из окружения.
    systemProperty("api.version", "1.44")

    // На Windows Testcontainers промахивается мимо активного docker-контекста и стучится
    // в дефолтный npipe. Подставляем рабочий endpoint, если DOCKER_HOST не задан снаружи;
    // на Linux/CI ветка не срабатывает.
    if (System.getenv("DOCKER_HOST") == null && System.getProperty("os.name").startsWith("Windows")) {
        environment("DOCKER_HOST", "npipe:////./pipe/dockerDesktopLinuxEngine")
    }
}
