package org.iglaz.battlereport.pages

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/feed.jsx
 * │ @design-part     Feed
 * │ @design-note     Список, maxWidth 860px по центру.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.autoLength
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.iglaz.battlereport.components.sections.ReportCard
import org.iglaz.battlereport.model.AppState
import org.iglaz.battlereport.theme.ContainerStyle
import org.iglaz.battlereport.theme.T

/** Лента: одна колонка карточек, максимум 860px, по центру. */
@Page
@Composable
fun HomePage() {
    Column(ContainerStyle.toModifier().padding(top = 32.px, bottom = 80.px)) {
        Column(
            Modifier.fillMaxWidth().maxWidth(860.px).margin(leftRight = autoLength).gap(T.FeedGap)
        ) {
            AppState.reports.sortedByDescending { it.date }.forEach { ReportCard(it) }
        }
    }
}
