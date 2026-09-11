package org.isglaz.battlereport.components.widgets

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/ui.jsx
 * │ @design-part     formatDate() · timeAgo() · fmtNum()
 * │ @design-note     "Today" is pinned to 2026-06-06 in the prototype for stable seeds.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
 */

import org.isglaz.battlereport.i18n.I18n
import org.isglaz.battlereport.i18n.Lang
import org.isglaz.battlereport.i18n.l
import kotlin.js.Date

/** Port of formatDate / timeAgo / fmtNum from br/ui.jsx, with the locale from I18n. */
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
