package org.isglaz.battlereport.data

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/data.jsx, br/app.jsx
 * │ @design-part     window.BR_DATA
 * │ @design-note     В прототипе данных нет по сети — этот слой добавлен для переезда на Ktor.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.isglaz.battlereport.model.*

/**Kobweb-сервер — тогда BASE =
 *  * Единственное место, которое знает про сеть. Сейчас отдаёт SampleData;
 *  * раскомментируйте fetch-ветки, когда на сервере появятся соответствующие роуты.
 *  *
 *  * Ваш Ktor слушает :8080 (server/src/main/kotlin/Routing.kt). В деве удобнее
 *  * не хардкодить хост, а проксировать /api через "/api".
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
