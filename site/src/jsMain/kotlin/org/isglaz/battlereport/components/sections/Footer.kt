package org.isglaz.battlereport.components.sections

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/footer.jsx
 * │ @design-part     Footer · LangBtn
 * │ @design-note     Один ряд: Support слева, язык справа. Паддинги 18px. Скрыт на /new.
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
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.isglaz.battlereport.i18n.*
import org.isglaz.battlereport.theme.*

/** Футер: «Support» слева, переключатель языка справа. Низкий — паддинги 18px. */
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
