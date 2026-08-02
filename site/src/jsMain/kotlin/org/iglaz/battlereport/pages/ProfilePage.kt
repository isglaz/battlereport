package org.iglaz.battlereport.pages

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/profile.jsx
 * │ @design-part     Profile
 * │ @design-note     Баннер с гексовой текстурой; в прототипе SVG-pattern, здесь CSS-градиенты.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.iglaz.battlereport.components.sections.ReportCard
import org.iglaz.battlereport.components.widgets.*
import org.iglaz.battlereport.i18n.L
import org.iglaz.battlereport.model.*
import org.iglaz.battlereport.theme.*

@Page("/users/{id}")
@Composable
fun ProfilePage() {
    val ctx = rememberPageContext()
    val user = SampleData.users[ctx.route.params["id"]] ?: SampleData.me
    val theirs = AppState.reports.filter { it.authorId == user.id }.sortedByDescending { it.date }

    Column(Modifier.fillMaxWidth().padding(bottom = 90.px)) {
        // баннер с гексовой текстурой
        Box(
            Modifier.fillMaxWidth().height(132.px).backgroundColor(T.Surface2)
                .borderBottom(1.px, LineStyle.Solid, T.Border)
                .styleModifier {
                    property("background-image",
                        "repeating-linear-gradient(60deg, oklch(0.93 0 0) 0 1px, transparent 1px 34px)," +
                        "repeating-linear-gradient(-60deg, oklch(0.93 0 0) 0 1px, transparent 1px 34px)")
                }
        )

        Column(ContainerStyle.toModifier().maxWidth(860.px)) {
            Row(Modifier.fillMaxWidth().margin(top = 22.px), verticalAlignment = Alignment.Bottom) {
                Avatar(user, 92, ring = true)
                Spacer()
                if (user.id == SampleData.me.id) {
                    Btn({}, BtnKind.Ghost, Modifier.margin(bottom = 6.px)) { SpanText(L("editProfile")) }
                }
            }

            Column(Modifier.margin(top = 16.px).gap(3.px)) {
                SpanText(user.name, Modifier.fontSize(28.px).fontWeight(600).lineHeight(1.1))
                SpanText("${user.handle} · ${L("joined", "year" to user.joined)}",
                    Modifier.fontSize(14.5.px).color(T.Muted))
            }

            if (user.bio.isNotEmpty()) {
                SpanText(user.bio, Modifier.maxWidth(620.px).margin(top = 20.px)
                    .fontSize(16.px).color(T.Ink2).lineHeight(1.6))
            }

            Divider(Modifier.margin(top = 28.px, bottom = 24.px))

            Row(Modifier.gap(6.px).margin(bottom = 18.px), verticalAlignment = Alignment.Bottom) {
                SpanText(
                    if (user.id == SampleData.me.id) L("yourReports") else L("reportsBy", "name" to user.name),
                    Modifier.fontSize(18.px).fontWeight(600)
                )
                SpanText("· ${theirs.size}", Modifier.color(T.Faint).fontSize(18.px))
            }

            Column(Modifier.fillMaxWidth().gap(T.FeedGap)) {
                theirs.forEach { ReportCard(it, hideAuthor = true) }
                if (theirs.isEmpty()) {
                    SpanText(L("noReportsYet"), Modifier.fillMaxWidth().textAlign(TextAlign.Center)
                        .padding(topBottom = 40.px).color(T.Faint))
                }
            }
        }
    }
}
