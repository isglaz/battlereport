package org.isglaz.battlereport.theme

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/styles.css
 * │ @design-part     :root (токены) · .btn/.btn-primary/.btn-ghost/.btn-quiet/.btn-soft · .card · .chip · .input · .md · .container
 * │ @design-note     Все значения — прямые копии oklch-констант. Меняете цвет в styles.css → меняете здесь в object T.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Правки визуала делаются СНАЧАЛА в файлах прототипа выше, потом переносятся сюда:
 * прототип остаётся источником истины по дизайну. Полная карта — DESIGN-MAP.md.
 */

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.selectors.active
import com.varabyte.kobweb.silk.style.selectors.focus
import com.varabyte.kobweb.silk.style.selectors.hover
import org.jetbrains.compose.web.css.*

/**
 * Порт br/styles.css. Значения — ровно те же oklch-константы, что в прототипе:
 * монохром, единственный цвет во всём интерфейсе — фон аватара.
 */
object T {
    val Ink = Color("oklch(0.2 0 0)")
    val Ink2 = Color("oklch(0.36 0 0)")
    val Muted = Color("oklch(0.52 0 0)")
    val Faint = Color("oklch(0.66 0 0)")

    val Bg = Color("#ffffff")
    val Surface = Color("oklch(0.984 0.004 290)")
    val Surface2 = Color("oklch(0.975 0 0)")
    val Border = Color("oklch(0.912 0 0)")
    val BorderStrong = Color("oklch(0.84 0 0)")

    val Accent = Ink
    val AccentHover = Color("oklch(0.37 0 0)")
    val AccentPress = Color("oklch(0.18 0 0)")
    val AccentSoft = Color("oklch(0.955 0 0)")
    val AccentSoftBd = Color("oklch(0.9 0 0)")
    val Danger = Color("oklch(0.55 0.18 25)")

    val ShSm = "0 1px 2px rgb(0 0 0 / 4%)"
    val ShMd = "0 4px 14px rgb(0 0 0 / 7%)"
    val ShLg = "0 12px 34px rgb(0 0 0 / 12%)"

    const val FontBody = "\"Source Serif 4\", Georgia, serif"
    val HeaderH = 64.px
    val Radius = 12.px
    val RadiusSm = 8.px
    val PadCard = 22.px
    val FeedGap = 16.px
    val MaxW = 1180.px
}

/** Центрированный контейнер (.container). Страница отчёта его НЕ использует — см. ReportPage. */
val ContainerStyle = CssStyle.base {
    Modifier.fillMaxWidth().maxWidth(T.MaxW).margin(leftRight = autoLength).padding(leftRight = 28.px)
}

val AppRootStyle = CssStyle.base {
    Modifier.minHeight(100.vh).display(DisplayStyle.Flex).flexDirection(FlexDirection.Column)
}

/* ---- .btn и варианты ---- */
val BtnStyle = CssStyle.base {
    Modifier
        .display(DisplayStyle.LegacyInlineFlex).alignItems(AlignItems.Center).gap(8.px)
        .fontSize(14.5.px).fontWeight(500).lineHeight(1)
        .border(1.px, LineStyle.Solid, Colors.Transparent).borderRadius(9.px)
        .padding(topBottom = 10.px, leftRight = 16.px)
        .whiteSpace(WhiteSpace.NoWrap).cursor(Cursor.Pointer)
        .transition(Transition.group(listOf("background", "color", "border-color", "box-shadow"), 160.ms))
}

val BtnPrimaryStyle = CssStyle {
    base { Modifier.backgroundColor(T.Accent).color(Colors.White) }
    hover { Modifier.backgroundColor(T.AccentHover) }
    active { Modifier.backgroundColor(T.AccentPress) }
}

val BtnGhostStyle = CssStyle {
    base { Modifier.backgroundColor(Colors.Transparent).color(T.Ink2).border(1.px, LineStyle.Solid, T.BorderStrong) }
    hover { Modifier.backgroundColor(T.Surface2).styleModifier { borderColor(T.Faint) } }
}

val BtnQuietStyle = CssStyle {
    base { Modifier.backgroundColor(Colors.Transparent).color(T.Muted) }
    hover { Modifier.color(T.Ink).backgroundColor(T.Surface2) }
}

val BtnSoftStyle = CssStyle {
    base { Modifier.backgroundColor(T.Surface2).color(T.Ink2).border(1.px, LineStyle.Solid, T.Border) }
    hover { Modifier.styleModifier { borderColor(T.BorderStrong) }.color(T.Ink) }
}

/** Текстовая кнопка «New Report» в шапке — без рамки и фона. */
val BtnTextStyle = CssStyle {
    base {
        Modifier.display(DisplayStyle.LegacyInlineFlex).alignItems(AlignItems.Center).gap(6.px)
            .backgroundColor(Colors.Transparent).border(0.px).padding(0.px)
            .fontSize(16.px).fontWeight(500).letterSpacing((-0.01).em).lineHeight(1)
            .color(T.Muted).cursor(Cursor.Pointer).transition(Transition.of("color", 150.ms))
    }
    hover { Modifier.color(T.Ink) }
}

/* ---- .card ---- */
val CardStyle = CssStyle.base {
    Modifier.backgroundColor(T.Bg)
        .border(1.px, LineStyle.Solid, T.Border)
        .borderRadius(T.Radius)
        .styleModifier { property("box-shadow", T.ShSm) }
}

/** Карточка-ссылка: наведение поднимает и усиливает тень (лента, сетка варгеймов). */
val CardHoverStyle = CssStyle {
    base { Modifier.transition(Transition.group(listOf("box-shadow", "border-color", "transform"), 180.ms)) }
    hover {
        Modifier.translateY((-2).px)
            .styleModifier { borderColor(T.BorderStrong); property("box-shadow", T.ShMd) }
    }
}

val ChipStyle = CssStyle.base {
    Modifier.display(DisplayStyle.LegacyInlineFlex).alignItems(AlignItems.Center).gap(6.px)
        .fontSize(12.5.px).fontWeight(500)
        .padding(topBottom = 4.px, leftRight = 11.px).borderRadius(999.px)
        .backgroundColor(T.Surface2).color(T.Ink2)
        .border(1.px, LineStyle.Solid, T.Border)
}

val InputStyle = CssStyle {
    base {
        Modifier.fillMaxWidth().backgroundColor(T.Bg)
            .border(1.px, LineStyle.Solid, T.BorderStrong).borderRadius(T.RadiusSm)
            .padding(topBottom = 11.px, leftRight = 13.px)
            .fontSize(15.px).color(T.Ink)
            .transition(Transition.group(listOf("border-color", "box-shadow"), 150.ms))
    }
    focus {
        Modifier.outline(0.px, LineStyle.None, Colors.Transparent)
            .styleModifier { borderColor(T.Accent); property("box-shadow", "0 0 0 3px rgb(0 0 0 / 10%)") }
    }
}

/** Заголовок-надпись над значением в сайдбаре: 11.5px, uppercase, трекинг. */
val EyebrowStyle = CssStyle.base {
    Modifier.fontSize(11.5.px).textTransform(TextTransform.Uppercase)
        .letterSpacing(0.06.em).color(T.Faint)
}

/** Рендер markdown (.md из styles.css). */
val MarkdownStyle = CssStyle {
    base { Modifier.color(T.Ink2).fontSize(16.5.px).lineHeight(1.68) }
    cssRule(" h1") { Modifier.fontSize(1.9.em).margin(top = 1.1.em, bottom = 0.5.em).color(T.Ink).fontWeight(600) }
    cssRule(" h2") { Modifier.fontSize(1.45.em).margin(top = 1.3.em, bottom = 0.5.em).color(T.Ink).fontWeight(600) }
    cssRule(" h3") { Modifier.fontSize(1.2.em).margin(top = 1.2.em, bottom = 0.4.em).color(T.Ink).fontWeight(600) }
    cssRule(" p") { Modifier.margin(top = 0.px, bottom = 1.em) }
    cssRule(" ul, ol") { Modifier.margin(bottom = 1.em).padding(left = 22.px) }
    cssRule(" li") { Modifier.margin(bottom = 0.35.em) }
    cssRule(" strong") { Modifier.color(T.Ink).fontWeight(600) }
    cssRule(" blockquote") {
        Modifier.margin(topBottom = 1.em, leftRight = 0.px).padding(left = 18.px)
            .borderLeft(3.px, LineStyle.Solid, T.AccentSoftBd).color(T.Muted)
    }
    cssRule(" hr") { Modifier.border(0.px).borderTop(1.px, LineStyle.Solid, T.Border).margin(topBottom = 1.6.em) }
    cssRule(" img") { Modifier.fillMaxWidth().borderRadius(T.RadiusSm).display(DisplayStyle.Block) }
}

private fun Color(v: String) = org.jetbrains.compose.web.css.Color(v)
