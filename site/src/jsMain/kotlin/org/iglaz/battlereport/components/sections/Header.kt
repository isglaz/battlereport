package org.iglaz.battlereport.components.sections

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/header.jsx
 * │ @design-part     Header · NewReportLink · MenuItem
 * │ @design-note     Табы и поиск из шапки удалены. Навигация: лого, карточки, ссылки в отчёте.
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
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.icons.fa.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.*
import org.iglaz.battlereport.components.widgets.*
import org.iglaz.battlereport.i18n.L
import org.iglaz.battlereport.model.SampleData
import org.iglaz.battlereport.theme.*

/**
 * Шапка во всю ширину: лого слева, «New Report» + аватар справа.
 * Центральных табов и поиска в шапке НЕТ — так решено в дизайне.
 */
@Composable
fun Header() {
    val ctx = rememberPageContext()
    val me = SampleData.me
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        Modifier.fillMaxWidth().height(T.HeaderH).padding(leftRight = 40.px)
            .position(Position.Sticky).top(0.px).zIndex(40)
            .backgroundColor(Color("oklch(0.984 0.004 290 / 0.82)"))
            .borderBottom(1.px, LineStyle.Solid, T.Border)
            .styleModifier {
                property("backdrop-filter", "saturate(180%) blur(14px)")
                property("-webkit-backdrop-filter", "saturate(180%) blur(14px)")
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(Modifier.cursor(Cursor.Pointer).onClick { ctx.router.navigateTo("/") }) {
            SpanText("Battle", Modifier.fontSize(25.px).fontWeight(600).letterSpacing((-0.02).em))
            SpanText("Report", Modifier.fontSize(25.px).fontWeight(400).letterSpacing((-0.02).em).color(T.Muted))
        }

        Spacer()

        Row(Modifier.gap(22.px), verticalAlignment = Alignment.CenterVertically) {
            Btn({ ctx.router.navigateTo("/new") }, BtnKind.Text) {
                FaPlus(Modifier.margin(top = (-1).px), size = IconSize.XS)
                SpanText(L("newReport"))
            }
            Box {
                Avatar(me, 40, ring = true) { menuOpen = !menuOpen }
                if (menuOpen) UserMenu(onClose = { menuOpen = false })
            }
        }
    }
}

@Composable
private fun UserMenu(onClose: () -> Unit) {
    val ctx = rememberPageContext()
    val me = SampleData.me
    Column(
        CardStyle.toModifier()
            .position(Position.Absolute).right(0.px).top(50.px).width(220.px).padding(8.px)
            .zIndex(50).styleModifier { property("box-shadow", T.ShLg) }
    ) {
        Row(Modifier.padding(topBottom = 8.px, leftRight = 10.px).gap(10.px),
            verticalAlignment = Alignment.CenterVertically) {
            Avatar(me, 38)
            Column {
                SpanText(me.name, Modifier.fontSize(14.5.px).fontWeight(500))
                SpanText(me.handle, Modifier.fontSize(12.5.px).color(T.Faint))
            }
        }
        Divider(Modifier.margin(topBottom = 4.px))
        MenuItem(L("myProfile")) { onClose(); ctx.router.navigateTo("/users/${me.id}") }
        MenuItem(L("notifications")) { onClose() }
        Divider(Modifier.margin(topBottom = 4.px))
        MenuItem(L("logout")) { onClose() }
    }
}

@Composable
private fun MenuItem(label: String, onClick: () -> Unit) {
    SpanText(label, MenuItemStyle.toModifier().onClick { onClick() })
}

private val MenuItemStyle = com.varabyte.kobweb.silk.style.CssStyle {
    base {
        Modifier.fillMaxWidth().padding(topBottom = 9.px, leftRight = 10.px).borderRadius(9.px)
            .color(T.Ink2).fontSize(14.px).fontWeight(500).cursor(Cursor.Pointer)
    }
    hover { Modifier.backgroundColor(T.Surface2) }
}

private fun Color(v: String) = org.jetbrains.compose.web.css.Color(v)
