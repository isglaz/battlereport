package org.isglaz.battlereport.components.sections

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/feed.jsx
 * │ @design-part     ReportCard
 * │ @design-note     hideAuthor=true is used on the profile; layout=grid from the prototype was not ported.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.fa.FaDiceD6
import com.varabyte.kobweb.silk.components.icons.fa.IconSize
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.isglaz.battlereport.components.widgets.*
import org.isglaz.battlereport.model.*
import org.isglaz.battlereport.theme.*

/** Report card in the feed and in the profile / wargame lists. */
@Composable
fun ReportCard(report: Report, hideAuthor: Boolean = false) {
    val ctx = rememberPageContext()
    val author = SampleData.users[report.authorId]
    val game = SampleData.wargames[report.wargameId]

    Card(
        Modifier.padding(T.PadCard).gap(14.px),
        hover = true,
        onClick = { ctx.router.navigateTo("/reports/${report.id}") },
    ) {
        if (!hideAuthor && author != null) {
            Row(Modifier.gap(9.px), verticalAlignment = Alignment.CenterVertically) {
                Avatar(author, 38)
                Column(Modifier.lineHeight(1.25)) {
                    SpanText(author.name, Modifier.fontSize(14.5.px).fontWeight(500))
                    SpanText(formatDate(report.date), Modifier.fontSize(12.5.px).color(T.Faint))
                }
            }
        }

        Column(Modifier.fillMaxWidth().gap(8.px)) {
            game?.let {
                Chip(it.title, onClick = { ctx.router.navigateTo("/wargames/${it.id}") })
            }
            SpanText(report.title, Modifier.fontSize(22.px).fontWeight(600).lineHeight(1.2))
            SpanText(report.preview, Modifier.color(T.Muted).fontSize(15.px).lineHeight(1.6)
                .styleModifier {
                    property("display", "-webkit-box")
                    property("-webkit-line-clamp", "2")
                    property("-webkit-box-orient", "vertical")
                    property("overflow", "hidden")
                })
        }

        Divider()
        StatRow(
            likes = AppState.likeCount(report),
            comments = AppState.commentCount(report.id),
            views = report.views,
            date = if (hideAuthor) report.date else null,
            liked = AppState.isLiked(report),
            onLike = { AppState.toggleLike(report) },
        )
    }
}
