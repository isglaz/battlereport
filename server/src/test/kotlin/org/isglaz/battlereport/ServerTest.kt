package org.isglaz.battlereport

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Поднимает приложение целиком поверх Postgres из Testcontainers: проверяется и то,
 * что changelog накатывается на чистую БД, и то, что сервер стартует на реальном Postgres.
 */
class ServerTest : DatabaseTestBase() {

    @Test
    fun `test root endpoint`() = testApplication {
        // Координаты контейнера подставляются поверх дефолтов из application.conf.
        environment {
            config = MapApplicationConfig(
                "db.url" to r2dbcUrl(),
                "db.user" to postgres.username,
                "db.password" to postgres.password,
            )
        }
        configure()

        assertEquals(HttpStatusCode.OK, client.get("/").status)
    }
}
