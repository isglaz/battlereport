package org.isglaz.battlereport.pages.wargames

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/profile.jsx
 * │ @design-part     WargamesIndex
 * │ @design-note     Заголовок «Wargames» и подпись удалены из дизайна — не возвращайте.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignSelf
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.isglaz.battlereport.components.widgets.*
import org.isglaz.battlereport.i18n.L
import org.isglaz.battlereport.i18n.l
import org.isglaz.battlereport.model.*
import org.isglaz.battlereport.theme.*

/**
 * Список варгеймов. Заголовка страницы нет — сразу поиск по центру, потом сетка.
 * Поиск идёт по названию, издателю, дизайнеру и году.
 */
@Page("/wargames")
@Composable
fun WargamesPage() {
    val ctx = rememberPageContext()
    var q by remember { mutableStateOf("") }
    val term = q.trim().lowercase()
    val list = SampleData.wargames.values.filter {
        term.isEmpty() || "${it.title} ${it.publisher} ${it.designer} ${it.year}".lowercase().contains(term)
    }

    Column(ContainerStyle.toModifier().padding(top = 32.px, bottom = 80.px)) {
        SearchField(q, l("searchWargames")) { q = it }

        if (list.isEmpty()) {
            SpanText(L("noWargamesMatch", "q" to q), Modifier.color(T.Muted).fontSize(15.5.px))
        }

        SimpleGrid(numColumns(base = 1, sm = 2, lg = 3), Modifier.fillMaxWidth().gap(T.FeedGap)) {
            list.forEach { g ->
                Card(Modifier.fillMaxHeight().overflow(Overflow.Hidden), hover = true,
                    onClick = { ctx.router.navigateTo("/wargames/${g.id}") }) {
                    Placeholder(g.boxLabel, Modifier.fillMaxWidth().height(150.px))
                    Column(Modifier.padding(18.px).gap(3.px)) {
                        SpanText(g.title, Modifier.fontSize(17.px).fontWeight(600))
                        SpanText("${g.year} · ${g.publisher}", Modifier.fontSize(13.px).color(T.Muted))
                        Chip(L("reportCount", "n" to AppState.reports.count { it.wargameId == g.id }),
                            Modifier.margin(top = 9.px).alignSelf(AlignSelf.Start))
                    }
                }
            }
        }
    }
}
