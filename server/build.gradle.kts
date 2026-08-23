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
    implementation(libs.h2database.h2)
    implementation(libs.h2database.r2dbc)
    implementation(libs.logback)
    // JDBC-драйвер нужен тестам и Liquibase; рантайм сервера ходит в БД через R2DBC.
    implementation(libs.postgresql)
    implementation(libs.postgresql.r2dbc)

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)

    // Интеграционные тесты поднимают Postgres в Testcontainers и накатывают на него
    // ровно те же миграции, что уедут в прод: changelog приезжает в classpath из :migrations.
    testImplementation(project(":migrations"))
    testImplementation(libs.liquibase.core)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junitJupiter)
    testImplementation(libs.testcontainers.postgresql)
}

tasks.test {
    useJUnitPlatform()
}