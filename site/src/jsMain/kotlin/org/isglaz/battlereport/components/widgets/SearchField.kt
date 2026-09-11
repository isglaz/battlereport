package org.isglaz.battlereport.components.widgets

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/profile.jsx
 * │ @design-part     WargamesIndex -> search row
 * │ @design-note     Lives ONLY on /wargames. There is no search in the header - a deliberate decision.
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
 * Pill search row. In the design it lives ONLY on the wargame list page,
 * centered above the grid - there is no search in the header.
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
