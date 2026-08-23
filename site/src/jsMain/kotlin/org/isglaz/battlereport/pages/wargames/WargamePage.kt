package org.isglaz.battlereport.pages.wargames

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/profile.jsx
 * │ @design-part     WargameCard
 * │ @design-note     Рейтинг игры из дизайна убран.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignSelf
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.fa.FaArrowLeft
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.isglaz.battlereport.components.sections.ReportCard
import org.isglaz.battlereport.components.widgets.*
import org.isglaz.battlereport.i18n.L
import org.isglaz.battlereport.model.*
import org.isglaz.battlereport.theme.*

@Page("/wargames/{id}")
@Composable
fun WargamePage() {
    val ctx = rememberPageContext()
    val game = SampleData.wargames[ctx.route.params["id"]] ?: return
    val related = AppState.reports.filter { it.wargameId == game.id }.sortedByDescending { it.date }

    Column(ContainerStyle.toModifier().maxWidth(920.px).padding(top = 24.px, bottom = 90.px)) {
        Btn({ ctx.router.navigateTo("/wargames") }, BtnKind.Quiet,
            Modifier.margin(bottom = 18.px).alignSelf(AlignSelf.Start)) {
            FaArrowLeft(size = IconSize.SM); SpanText(L("allWargames"))
        }

        Card(Modifier.padding(30.px)) {
            Row(Modifier.fillMaxWidth().gap(30.px), verticalAlignment = Alignment.Top) {
                Placeholder(game.boxLabel, Modifier.width(220.px).height(280.px).flexShrink(0)
                    .borderRadius(T.RadiusSm))
                Column(Modifier.flexGrow(1).minWidth(0.px)) {
                    SpanText(game.title, Modifier.fontSize(32.px).fontWeight(600).lineHeight(1.12))
                    SpanText(game.tagline, Modifier.margin(top = 8.px).fontSize(15.5.px).fontWeight(500))

                    Row(Modifier.gap(26.px).margin(top = 20.px, bottom = 22.px)) {
                        Stat(L("released"), game.year.toString())
                        Stat(L("reports"), related.size.toString())
                    }

                    SpanText(game.desc, Modifier.fontSize(15.5.px).color(T.Ink2).lineHeight(1.65))

                    Divider(Modifier.margin(top = 22.px, bottom = 18.px))
                    Row(Modifier.gap(24.px).fontSize(14.px)) {
                        Meta(L("publisher"), game.publisher)
                        Meta(L("designer"), game.designer)
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth().margin(top = 36.px, bottom = 18.px), verticalAlignment = Alignment.Bottom) {
            SpanText(L("reportsFor", "title" to game.title), Modifier.fontSize(20.px).fontWeight(600))
            Spacer()
            Chip(L("reportCount", "n" to related.size))
        }

        Column(Modifier.fillMaxWidth().gap(T.FeedGap)) {
            related.forEach { ReportCard(it) }
            if (related.isEmpty()) {
                SpanText(L("noReportsForGame"), Modifier.fillMaxWidth().textAlign(TextAlign.Center)
                    .padding(topBottom = 40.px).color(T.Faint))
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        SpanText(value, Modifier.fontSize(19.px).fontWeight(500))
        SpanText(label, Modifier.fontSize(12.5.px).color(T.Faint))
    }
}

@Composable
private fun Meta(label: String, value: String) {
    Column {
        SpanText(label, Modifier.color(T.Faint))
        SpanText(value, Modifier.fontWeight(500))
    }
}
