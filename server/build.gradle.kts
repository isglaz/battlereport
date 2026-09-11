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

    // Integration tests start Postgres in Testcontainers and apply to it exactly the same
    // migrations that will go to production: Liquibase reads the changelog straight from the
    // migrations/liquibase directory, so no wrapper module is needed.
    // Liquibase talks to the database over JDBC only, hence the driver in the test dependencies.
    testImplementation(libs.postgresql)
    testImplementation(libs.liquibase.core)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.junitJupiter)
    testImplementation(libs.testcontainers.postgresql)
}

tasks.test {
    useJUnitPlatform()

    // Docker Desktop 29 answers with an empty 400 to the version-less API requests sent by
    // docker-java inside Testcontainers (the daemon advertises min API 1.40). docker-java
    // reads the version from the api.version system property, not from the environment.
    systemProperty("api.version", "1.44")

    // On Windows Testcontainers misses the active docker context and knocks on the default
    // npipe. Substitute a working endpoint when DOCKER_HOST is not set from outside;
    // on Linux/CI this branch does not trigger.
    if (System.getenv("DOCKER_HOST") == null && System.getProperty("os.name").startsWith("Windows")) {
        environment("DOCKER_HOST", "npipe:////./pipe/dockerDesktopLinuxEngine")
    }
}
