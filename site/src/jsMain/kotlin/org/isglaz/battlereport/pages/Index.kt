package org.isglaz.battlereport.pages

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/feed.jsx
 * │ @design-part     Feed
 * │ @design-note     A list, maxWidth 860px, centered.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.autoLength
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.isglaz.battlereport.components.sections.ReportCard
import org.isglaz.battlereport.model.AppState
import org.isglaz.battlereport.theme.ContainerStyle
import org.isglaz.battlereport.theme.T

/** Feed: a single column of cards, at most 860px, centered. */
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
