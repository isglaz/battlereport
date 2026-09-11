package org.isglaz.battlereport.data

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/data.jsx, br/app.jsx
 * │ @design-part     window.BR_DATA
 * │ @design-note     The prototype has no data over the network - this layer was added for the move to Ktor.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
 */

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.isglaz.battlereport.model.*

/**Kobweb server - then BASE =
 *  * The only place that knows about the network. For now it returns SampleData;
 *  * uncomment the fetch branches once the matching routes appear on the server.
 *  *
 *  * Your Ktor listens on :8080 (server/src/main/kotlin/Routing.kt). In dev it is more
 *  * convenient not to hardcode the host but to proxy /api through "/api".
 */
object Api {
    private const val BASE = "http://localhost:8080"
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun reports(): List<Report> = SampleData.reports
    // suspend fun reports(): List<Report> = get("/reports")

    suspend fun comments(): Map<String, List<Comment>> = SampleData.comments

    suspend fun wargames(): Map<String, Wargame> = SampleData.wargames

    suspend fun users(): Map<String, User> = SampleData.users

    suspend fun publish(draft: Draft): Report? = null
    // suspend fun publish(draft: Draft): Report = post("/reports", draft)

    private suspend inline fun <reified R> get(path: String): R {
        val res = window.fetch(BASE + path).await()
        return json.decodeFromString(res.text().await())
    }
}
