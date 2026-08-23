package org.isglaz.battlereport.components.widgets

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/profile.jsx
 * │ @design-part     WargamesIndex → строка поиска
 * │ @design-note     Живёт ТОЛЬКО на /wargames. В шапке поиска нет — это осознанное решение.
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
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.icons.fa.FaMagnifyingGlass
import com.varabyte.kobweb.silk.components.icons.fa.FaXmark
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Input
import org.isglaz.battlereport.theme.T

/**
 * Pill-строка поиска. В дизайне живёт ТОЛЬКО на странице списка варгеймов,
 * по центру над сеткой — в шапке поиска нет.
 */
@Composable
fun SearchField(value: String, placeholder: String, onChange: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().maxWidth(420.px).height(42.px)
            .margin(leftRight = autoLength, bottom = 24.px)
            .padding(leftRight = 15.px).gap(9.px)
            .backgroundColor(T.Bg).border(1.px, LineStyle.Solid, T.Border).borderRadius(999.px),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FaMagnifyingGlass(Modifier.color(T.Faint).flexShrink(0))
        Input(InputType.Text, attrs = Modifier
            .flexGrow(1).minWidth(0.px).border(0.px)
            .outline(0.px, LineStyle.None, Colors.Transparent)
            .backgroundColor(Colors.Transparent).fontSize(15.px).color(T.Ink)
            .toAttrs {
                value(value)
                attr("placeholder", placeholder)
                onInput { onChange(it.value) }
                onKeyDown { if (it.key == "Escape") onChange("") }
            })
        if (value.isNotEmpty()) FaXmark(Modifier.color(T.Faint).cursor(Cursor.Pointer).onClick { onChange("") })
    }
}
