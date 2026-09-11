package org.isglaz.battlereport.theme

/*
 * ┌─ DESIGN SOURCE ─────────────────────────────────────────────────────────
 * │ @design-project  BattleReport (Omelette) → BattleReport.html
 * │ @design-file     br/styles.css
 * │ @design-part     html/body base · h1–h5 · a / a:hover · ::selection
 * │ @design-note     The "design tokens & base" section at the top of styles.css.
 * └─────────────────────────────────────────────────────────────────────────
 *
 * Visual changes are made FIRST in the prototype files listed above, then ported here:
 * the prototype stays the source of truth for design. Full map: DESIGN-MAP.md.
 */

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import org.jetbrains.compose.web.css.*

/**
 * Base document styles. Replaces your current initStyles in AppEntry.kt
 * (that one only set fillMaxHeight).
 */
@InitSilk
fun initBattleReportStyles(ctx: InitSilkContext) {
    ctx.stylesheet.registerStyleBase("html, body") {
        Modifier.fillMaxHeight().margin(0.px).padding(0.px)
            .backgroundColor(T.Bg).color(T.Ink)
            .fontFamily("Source Serif 4", "Georgia", "serif")
            .fontSize(16.px).lineHeight(1.55)
            .styleModifier {
                property("-webkit-font-smoothing", "antialiased")
                property("text-rendering", "optimizeLegibility")
            }
    }
    ctx.stylesheet.registerStyleBase("h1, h2, h3, h4, h5") {
        Modifier.margin(0.px).fontWeight(600).letterSpacing((-0.01).em)
            .styleModifier { property("text-wrap", "balance") }
    }
    ctx.stylesheet.registerStyleBase("p") {
        Modifier.styleModifier { property("text-wrap", "pretty") }
    }
    // links: no default blue - the user can add an <a> at any moment
    ctx.stylesheet.registerStyleBase("a") {
        Modifier.color(T.Ink).textDecorationLine(TextDecorationLine.None)
    }
    ctx.stylesheet.registerStyle("a:hover") {
        base { Modifier.color(T.Ink) }
    }
    ctx.stylesheet.registerStyleBase("button, input, textarea") {
        Modifier.fontFamily("Source Serif 4", "Georgia", "serif")
    }
    ctx.stylesheet.registerStyleBase("::selection") {
        Modifier.backgroundColor(org.jetbrains.compose.web.css.Color("oklch(0.27 0 0 / 0.13)"))
    }
}
