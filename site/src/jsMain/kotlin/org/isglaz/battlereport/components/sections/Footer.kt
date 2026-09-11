package org.isglaz.battlereport.components.sections

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/footer.jsx
 * │ @design-part     Footer · LangBtn
 * │ @design-note     One row: Support on the left, language on the right. Padding 18px. Hidden on /new.
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
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.isglaz.battlereport.i18n.*
import org.isglaz.battlereport.theme.*

/** Footer: "Support" on the left, language switcher on the right. Low - padding 18px. */
@Composable
fun Footer() {
    Box(
        Modifier.fillMaxWidth().margin(top = 60.px)
            .borderTop(1.px, LineStyle.Solid, T.Border)
            .backgroundColor(T.Surface)
    ) {
        Row(ContainerStyle.toModifier().padding(topBottom = 18.px), verticalAlignment = Alignment.CenterVertically) {
            SpanText(L("support"), Modifier.fontSize(17.px).fontWeight(600).letterSpacing((-0.01).em))
            Spacer()
            Row(Modifier.gap(2.px), verticalAlignment = Alignment.CenterVertically) {
                LangBtn(Lang.RU)
                SpanText("/", Modifier.color(T.Faint).fontSize(13.px))
                LangBtn(Lang.EN)
            }
        }
    }
}

@Composable
private fun LangBtn(lang: Lang) {
    val active = I18n.lang == lang
    SpanText(
        lang.label,
        LangBtnStyle.toModifier()
            .color(if (active) T.Ink else T.Muted)
            .fontWeight(if (active) 600 else 400)
            .onClick { I18n.setLang(lang) }
    )
}

private val LangBtnStyle = CssStyle {
    base {
        Modifier.padding(topBottom = 4.px, leftRight = 8.px).fontSize(14.px)
            .cursor(Cursor.Pointer).transition(Transition.of("color", 150.ms))
    }
    hover { Modifier.color(T.Ink) }
}
