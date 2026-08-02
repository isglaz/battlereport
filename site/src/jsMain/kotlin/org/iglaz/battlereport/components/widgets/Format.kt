package org.iglaz.battlereport.components.widgets

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/ui.jsx
 * │ @design-part     formatDate() · timeAgo() · fmtNum()
 * │ @design-note     «Сегодня» в прототипе зафиксировано на 2026-06-06 для стабильных сидов.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import org.iglaz.battlereport.i18n.I18n
import org.iglaz.battlereport.i18n.Lang
import org.iglaz.battlereport.i18n.l
import kotlin.js.Date

/** Порт formatDate / timeAgo / fmtNum из br/ui.jsx, с локалью из I18n. */
fun formatDate(iso: String): String {
    val d = Date(if (iso.length == 10) "${iso}T12:00:00" else iso)
    val locale = if (I18n.lang == Lang.RU) "ru-RU" else "en-US"
    return d.toLocaleDateString(locale, dateOptions())
}

fun timeAgo(iso: String): String {
    val d = Date(if (iso.length == 10) "${iso}T12:00:00" else iso)
    val days = ((Date().getTime() - d.getTime()) / 86_400_000.0).let { kotlin.math.round(it).toInt() }
    return when {
        days <= 0 -> l("today")
        days == 1 -> l("yesterday")
        days < 7 -> l("daysAgo", "n" to days)
        days < 30 -> l("weeksAgo", "n" to kotlin.math.round(days / 7.0).toInt())
        else -> formatDate(iso)
    }
}

fun fmtNum(n: Int): String =
    if (n >= 1000) "${((n / 100).toDouble() / 10)}k".replace(".0k", "k") else n.toString()

private fun dateOptions(): dynamic {
    val o = js("({})")
    o.month = "short"; o.day = "numeric"; o.year = "numeric"
    return o
}
