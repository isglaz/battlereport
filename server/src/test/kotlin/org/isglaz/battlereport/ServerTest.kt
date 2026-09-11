package org.isglaz.battlereport

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Starts the whole application on top of Postgres from Testcontainers: this checks both that
 * the changelog applies to a clean database and that the server starts against a real Postgres.
 */
class ServerTest : DatabaseTestBase() {

    @Test
    fun `test root endpoint`() = testApplication {
        // The container coordinates are substituted over the defaults from application.conf.
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
